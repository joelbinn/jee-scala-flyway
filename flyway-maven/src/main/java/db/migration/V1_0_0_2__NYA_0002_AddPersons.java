package db.migration;

import com.mysql.jdbc.JDBC4Connection;
import joelabs.migr.util.DbTestUtil;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;
import se.joelabs.bananas.entity.PersonEntity;

import javax.persistence.EntityManager;
import java.sql.Connection;

/**
 * Example of a Java-based migration.
 */
public class V1_0_0_2__NYA_0002_AddPersons implements JdbcMigration {
    public void migrate(Connection connection) {
        JDBC4Connection c = ((JDBC4Connection) connection);
        final EntityManager em = DbTestUtil.createEntityManager(c.getURL(), c.getUser(), "");
        DbTestUtil.withinTransaction(em, () -> {
            final PersonEntity p = new PersonEntity();
            p.name = "Obelix";
            em.persist(p);
            return null;
        });
    }
}