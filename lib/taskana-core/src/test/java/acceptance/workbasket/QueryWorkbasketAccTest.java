package acceptance.workbasket;

import java.sql.SQLException;
import java.util.List;

import org.h2.store.fs.FileUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.BaseQuery.SortDirection;
import pro.taskana.WorkbasketService;
import pro.taskana.WorkbasketSummary;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidRequestException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.WorkbasketType;
import pro.taskana.security.JAASRunner;

/**
 * Acceptance test for all "query workbasket by permission" scenarios.
 */
@RunWith(JAASRunner.class)
public class QueryWorkbasketAccTest extends AbstractAccTest {

    private static SortDirection asc = SortDirection.ASCENDING;
    private static SortDirection desc = SortDirection.DESCENDING;

    public QueryWorkbasketAccTest() {
        super();
    }

    @Test
    public void testQueryWorkbasketByDomain()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .domainIn("DOMAIN_B")
            .list();
        Assert.assertEquals(3L, results.size());
    }

    @Test
    public void testQueryWorkbasketByDomainAndType()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .domainIn("DOMAIN_A")
            .typeIn(WorkbasketType.PERSONAL)
            .list();
        Assert.assertEquals(6L, results.size());
    }

    @Test
    public void testQueryWorkbasketByName()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .nameIn("Gruppenpostkorb KSC")
            .list();
        Assert.assertEquals(1L, results.size());
        Assert.assertEquals("GPK_KSC", results.get(0).getKey());
    }

    @Test
    public void testQueryWorkbasketByNameStartsWith()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .nameLike("%Gruppenpostkorb KSC%")
            .list();
        Assert.assertEquals(6L, results.size());
    }

    @Test
    public void testQueryWorkbasketByNameContains()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .nameLike("%Teamlead%", "%Gruppenpostkorb KSC%")
            .list();
        Assert.assertEquals(8L, results.size());
    }

    @Test
    public void testQueryWorkbasketByNameContainsCaseInsensitive()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .nameLike("%TEAMLEAD%")
            .list();
        Assert.assertEquals(2L, results.size());
    }

    @Test
    public void testQueryWorkbasketByDescription()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .descriptionLike("%consectetur%", "%gruppen%")
            .orderByType(desc)
            .orderByDescription(asc)
            .list();
        Assert.assertEquals(11L, results.size());
    }

    @Test
    public void testQueryWorkbasketByOwnerLike()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .ownerLike("%an%", "%te%")
            .orderByOwner(asc)
            .list();
        Assert.assertEquals(3L, results.size());
    }

    @Test
    public void testQueryWorkbasketByKey()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .keyIn("GPK_KSC")
            .list();
        Assert.assertEquals(1L, results.size());
    }

    @Test
    public void testQueryWorkbasketByMultipleKeys()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .keyIn("GPK_KSC_1", "GPK_KSC_2")
            .list();
        Assert.assertEquals(2L, results.size());
    }

    @Test
    public void testQueryWorkbasketByMultipleKeysWithUnknownKey()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .keyIn("GPK_KSC_1", "GPK_Ksc_2", "GPK_KSC_3")
            .list();
        Assert.assertEquals(2L, results.size());
    }

    @Test
    public void testQueryWorkbasketByKeyContains()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .keyLike("%KSC%")
            .list();
        Assert.assertEquals(6L, results.size());
    }

    @Test
    public void testQueryWorkbasketByKeyContainsIgnoreCase()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .keyLike("%kSc%")
            .list();
        Assert.assertEquals(6L, results.size());
    }

    @Test
    public void testQueryWorkbasketByKeyOrNameContainsIgnoreCase()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .keyOrNameLike("%kSc%")
            .list();
        Assert.assertEquals(12L, results.size());
    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }

    @Test
    public void testQueryWorkbasketByNameStartsWithSortedByNameAscending()
        throws SQLException, NotAuthorizedException, InvalidRequestException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .nameLike("%Gruppenpostkorb KSC%")
            .orderByName(asc)
            .list();
        Assert.assertEquals(6L, results.size());
        Assert.assertEquals("GPK_KSC", results.get(0).getKey());

        // check sort order is correct
        WorkbasketSummary previousSummary = null;
        for (WorkbasketSummary wbSummary : results) {
            if (previousSummary != null) {
                Assert.assertTrue(wbSummary.getName().compareToIgnoreCase(
                    previousSummary.getName()) >= 0);
            }
            previousSummary = wbSummary;
        }

    }

    @Test
    public void testQueryWorkbasketByNameStartsWithSortedByNameDescending()
        throws SQLException, NotAuthorizedException, InvalidRequestException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .nameLike("basxet%")
            .orderByName(desc)
            .list();
        Assert.assertEquals(10L, results.size());
        // check sort order is correct
        WorkbasketSummary previousSummary = null;
        for (WorkbasketSummary wbSummary : results) {
            if (previousSummary != null) {
                Assert.assertTrue(wbSummary.getName().compareToIgnoreCase(
                    previousSummary.getName()) <= 0);
            }
            previousSummary = wbSummary;
        }
    }

    @Test
    public void testQueryWorkbasketByNameStartsWithSortedByKeyAscending()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, InvalidRequestException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .nameLike("basxet%")
            .orderByKey(asc)
            .list();
        Assert.assertEquals(10L, results.size());
        // check sort order is correct
        WorkbasketSummary previousSummary = null;
        for (WorkbasketSummary wbSummary : results) {
            if (previousSummary != null) {
                Assert.assertTrue(wbSummary.getKey().compareToIgnoreCase(
                    previousSummary.getKey()) >= 0);
            }
            previousSummary = wbSummary;
        }
    }

    @Test
    public void testQueryWorkbasketByNameStartsWithSortedByKeyDescending()
        throws SQLException, NotAuthorizedException, InvalidArgumentException, InvalidRequestException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .nameLike("basxet%")
            .orderByKey(desc)
            .list();
        Assert.assertEquals(10L, results.size());
        // check sort order is correct
        WorkbasketSummary previousSummary = null;
        for (WorkbasketSummary wbSummary : results) {
            if (previousSummary != null) {
                Assert.assertTrue(wbSummary.getKey().compareToIgnoreCase(
                    previousSummary.getKey()) <= 0);
            }
            previousSummary = wbSummary;
        }
    }

    @Test
    public void testQueryWorkbasketByCreated()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .createdWithin(todaysInterval())
            .list();
        Assert.assertEquals(26L, results.size());
    }

    @Test
    public void testQueryWorkbasketByModified()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        List<WorkbasketSummary> results = workbasketService.createWorkbasketQuery()
            .modifiedWithin(todaysInterval())
            .list();
        Assert.assertEquals(26L, results.size());
    }

}
