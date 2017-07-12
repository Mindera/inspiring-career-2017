package com.mindera.inspiringcareer.dataaccess.hibernate;

/**
 *
 * @author tpires
 */
import org.hibernate.dialect.PostgreSQL9Dialect;

import java.sql.Types;

public class JsonPostgreSQLDialect extends PostgreSQL9Dialect {

  public JsonPostgreSQLDialect() {
    super();

    registerColumnType(Types.JAVA_OBJECT, "json");
    this.registerHibernateType(Types.OTHER, "json");
  }
}
