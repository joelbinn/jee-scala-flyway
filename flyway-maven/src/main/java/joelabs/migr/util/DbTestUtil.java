/*
 * NYPS 2020
 * 
 * User: joel
 * Date: 2015-01-28
 * Time: 20:44
 */
package joelabs.migr.util;

import org.springframework.test.util.ReflectionTestUtils;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Configuration parameters set as JVM properties:
 * <ul>
 * <li>nyps.ddl: path to DDL file</li>
 * </ul>
 */
public abstract class DbTestUtil {

    private static final String COM_MYSQL_JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String ORG_HIBERNATE_DIALECT_MY_SQL_DIALECT = "org.hibernate.dialect.MySQLDialect";

    private static final String ORACLE_JDBC_ORACLE_DRIVER = "oracle.jdbc.OracleDriver";
    private static final String ORG_HIBERNATE_DIALECT_ORACLE9_DIALECT = "org.hibernate.dialect.Oracle9Dialect";

    private static final String ORG_H2_DRIVER = "org.h2.Driver";
    private static final String ORG_HIBERNATE_DIALECT_H2_DIALECT = "org.hibernate.dialect.H2Dialect";

    private static final String JDBC_DERBY_MEMORY_UNIT_TESTING_JPA = "jdbc:derby:memory:unit-testing-jpa";
    private static final String JDBC_IN_MEMORY_DB_CONNECTION = "jdbc:h2:mem:testdb";
    private static final String HIBERNATE_HBM2DDL_AUTO = "hibernate.hbm2ddl.auto";
    private static final String HIBERNATE_SHOW_SQL = "hibernate.show_sql";
    private static final String HIBERNATE_FORMAT_SQL = "hibernate.format_sql";
    private static final String HIBERNATE_USE_SQL_COMMENTS = "hibernate.use_sql_comments";
    private static final String HIBERNATE_CONNECTION_URL = "hibernate.connection.url";
    private static final String HIBERNATE_CONNECTION_DRIVER_CLASS = "hibernate.connection.driver_class";
    private static final String HIBERNATE_DIALECT = "hibernate.dialect";
    private static final String HIBERNATE_CONNECTION_USERNAME = "hibernate.connection.username";
    private static final String HIBERNATE_CONNECTION_PASSWORD = "hibernate.connection.password";
    private static final String HIBERNATE_CONNECTION_CHAR_SET = "hibernate.connection.charSet";
    private static final String HIBERNATE_ID_NEW_GENERATOR_MAPPINGS = "hibernate.id.new_generator_mappings";
    private static final String HIBERNATE_DEFAULT_SCHEMA = "hibernate.default_schema";

    private static final HashMap<String, String> DEFAULT_PROPS = new HashMap<>();

    static {
        DEFAULT_PROPS.put(HIBERNATE_HBM2DDL_AUTO, "none");
        DEFAULT_PROPS.put(HIBERNATE_SHOW_SQL, "true");
        DEFAULT_PROPS.put(HIBERNATE_FORMAT_SQL, "true");
        DEFAULT_PROPS.put(HIBERNATE_USE_SQL_COMMENTS, "false");
    }

    /**
     * Creates an entity manager with the specified properties.
     * <p/>
     * It scans for all @Entity-annotated classes in all packages below the specified packages to scan.
     *
     * @param persistenceUnitProperties the properties of the persistence unit
     * @return the entity manager
     */
    public static EntityManager createEntityManager(Map<String, String> persistenceUnitProperties) {
        return Persistence.createEntityManagerFactory("FlywayPU", persistenceUnitProperties).createEntityManager();
    }

    /**
     * Create entity manager.
     *
     * @param connectionUrl the connection URL to the database
     * @param user          user name for login to database
     * @param password      password for login to database
     * @return entity manager factory
     */
    public static EntityManager createEntityManager(String connectionUrl, String user, String password) {
        Map<String, String> properties = getPUProps(connectionUrl, user, password);
        properties.put(HIBERNATE_HBM2DDL_AUTO, System.getProperty(HIBERNATE_HBM2DDL_AUTO, "validate"));
        return DbTestUtil.createEntityManager(properties);
    }

    public static EntityManager createEntityManager(String connectionUrl, String user, String password, String hibernateMode) {
        String mode;
        if (hibernateMode.equals("create") || hibernateMode.equals("update")) {
            mode = hibernateMode;
        } else {
            throw new IllegalArgumentException("Incorrect input parameter for Hibernate mode");
        }
        Map<String, String> properties = getPUProps(connectionUrl, user, password);
        properties.put(HIBERNATE_HBM2DDL_AUTO, mode);
        return DbTestUtil.createEntityManager(properties);
    }

    public static void injectEntityManager(EntityManager entityManager, Object... targetsForInjection) {
        for (Object targetForInjection : targetsForInjection) {
            getFields(targetForInjection).stream()
                    .filter(field -> field.getAnnotation(Inject.class) != null).forEach(field -> {
                field.setAccessible(true);
                ReflectionTestUtils.setField(targetForInjection, field.getName(), entityManager);
            });
        }
    }

    private static List<Field> getFields(Object o) {
        List<Field> fields = new ArrayList<>();
        Class<?> c = o.getClass();
        while (c != null) {
            fields.addAll(Arrays.asList(c.getDeclaredFields()));
            c = c.getSuperclass();
        }

        return fields;
    }

    public static <T> T withinTransaction(EntityManager entityManager, Callable<T> action) throws RuntimeException {
        entityManager.getTransaction().begin();
        try {
            T result = action.call();
            entityManager.getTransaction().commit();
            return result;
        } catch (RuntimeException re) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw re;
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RuntimeException(e);
        } catch (Throwable t) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw t;
        }
    }

    public static <T> T withinTransactionThrowingException(EntityManager entityManager, Callable<T> action) throws Exception {
        entityManager.getTransaction().begin();
        try {
            T result = action.call();
            entityManager.getTransaction().commit();
            return result;
        } catch (Throwable t) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw t;
        }
    }


    public static Map<String, String> getPUProps(String connectionUrl, String user, String password) {
        Map<String, String> properties = getDefaultAndSystemProperties();

        // NOTE, make sure to sync parameters below (except connection.url, connection.username, connection.password,
        // show_sql, format_sql)with the parameters in the persistence.xml of the EAR.
        properties.put(HIBERNATE_CONNECTION_URL, connectionUrl);
        properties.put(HIBERNATE_CONNECTION_USERNAME, user);
        properties.put(HIBERNATE_CONNECTION_PASSWORD, password);
        properties.put(HIBERNATE_SHOW_SQL, System.getProperty(HIBERNATE_SHOW_SQL, "false"));
        properties.put(HIBERNATE_FORMAT_SQL, System.getProperty(HIBERNATE_FORMAT_SQL, "true"));

        // Make sure the parameters below are the same as in the persistence.xml in the EAR
        properties.put(HIBERNATE_CONNECTION_CHAR_SET, System.getProperty(HIBERNATE_CONNECTION_CHAR_SET, "UTF-8"));
        // properties.put("hibernate.ejb.naming_strategy", "org.hibernate.cfg.ImprovedNamingStrategy");
        properties.put(HIBERNATE_ID_NEW_GENERATOR_MAPPINGS, System.getProperty(HIBERNATE_ID_NEW_GENERATOR_MAPPINGS, "true"));

        setDriverProps(connectionUrl, properties, user);
        StringBuilder propertiesLogString =
                new StringBuilder("Using the following properties for the persistence unit:");
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            propertiesLogString.append("\n").append(entry.getKey()).append(": ").append(entry.getValue());
        }
        System.out.println(propertiesLogString.toString());
        return properties;
    }

    public static Map<String, String> getDefaultAndSystemProperties() {
        Map<String, String> properties = new HashMap<>();
        for (Map.Entry<Object, Object> entry : System.getProperties().entrySet()) {
            properties.put(entry.getKey() + "", entry.getValue() + "");
        }

        for (Map.Entry<String, String> entry : DEFAULT_PROPS.entrySet()) {
            properties.put(entry.getKey(), System.getProperty(entry.getKey(), entry.getValue()));
        }
        return properties;
    }

    private static void setDriverProps(String connectionUrl, Map<String, String> properties, String user) {
        if (connectionUrl == null) {
            return;
        }

        if (connectionUrl.startsWith("jdbc:mysql")) {
            properties.put(HIBERNATE_DIALECT, ORG_HIBERNATE_DIALECT_MY_SQL_DIALECT);
            properties.put(HIBERNATE_CONNECTION_DRIVER_CLASS, COM_MYSQL_JDBC_DRIVER);
        } else if (connectionUrl.startsWith("jdbc:oracle")) {
            properties.put(HIBERNATE_DIALECT, ORG_HIBERNATE_DIALECT_ORACLE9_DIALECT);
            properties.put(HIBERNATE_CONNECTION_DRIVER_CLASS, ORACLE_JDBC_ORACLE_DRIVER);
            properties.put(HIBERNATE_DEFAULT_SCHEMA, user);
        }
    }

}
