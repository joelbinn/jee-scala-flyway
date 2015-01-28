package db.migration;

import com.mysql.jdbc.JDBC4Connection;
import joelabs.migr.util.DbTestUtil;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;
import se.joelabs.bananas.entity.PersonEntity;

import javax.persistence.EntityManager;
import java.sql.Connection;
import java.util.List;

/**
 * Example of a Java-based migration.
 */
public class V1_0_0_3__NYA_0003_UpdatePersonNames implements JdbcMigration {
    public void migrate(Connection connection) {
        JDBC4Connection c = ((JDBC4Connection) connection);
        final EntityManager em = DbTestUtil.createEntityManager(c.getURL(), c.getUser(), "");
        DbTestUtil.withinTransaction(em, () -> {
            final List<PersonEntity> persons = em.createQuery("SELECT p FROM PersonEntity p", PersonEntity.class).getResultList();
            persons.stream().forEach(p -> p.name = p.name + " Svensson");
            return null;
        });
    }
}