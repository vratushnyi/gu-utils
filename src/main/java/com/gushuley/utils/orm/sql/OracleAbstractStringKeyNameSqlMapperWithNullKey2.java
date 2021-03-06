package com.gushuley.utils.orm.sql;

import java.sql.*;

import com.gushuley.utils.orm.ORMException;
import com.gushuley.utils.orm.impl.*;


public abstract class OracleAbstractStringKeyNameSqlMapperWithNullKey2<C extends AbstractStringKeyNameObject, X extends GenericContext>
extends AbstractKeyNameSqlMapper2<C, String, X> 
{
	public OracleAbstractStringKeyNameSqlMapperWithNullKey2(boolean _short, String string, String string2, String string3, SqlAttribute... attrs) {
		super(_short, string, string2, string3, attrs);
	}
	
	public OracleAbstractStringKeyNameSqlMapperWithNullKey2(String string, String string2, String string3, SqlAttribute... attrs) {
		super(string, string2, string3, attrs);
	}
	
	@Override
	protected void setKeyValue(PreparedStatement stm, int n, String key)
			throws SQLException {
		stm.setString(n, key);
	}
	
	public String createKey(ResultSet rs) throws SQLException {
		return rs.getString(this.idColumn);
	}
	
	@Override
	protected String getSelectSql() {
		StringBuilder attrs = new StringBuilder();
		if (attributes != null) {
			for (SqlAttribute a : attributes) {
				attrs.append(", " + a.getColumn());
			}
		}
		return "SELECT " + idColumn + " ," + nameColumn + attrs + " FROM " + getTableName()
				+ " WHERE NVL(" + idColumn + ", '-') = NVL(?, '-')";
	}
	
	@Override
	protected GetQueryCallback<C> getDeleteQueryCB() {
		return new GetQueryCallback<C>() {
			public String getSql() throws ORMException {
				return "DELETE FROM " + getTableName() + 
					" WHERE NVL(" + idColumn + ", '-') = NVL(?, '-')";
			}
	
			public void setParams(PreparedStatement stm, C obj)
					throws SQLException {
				setKeyValue(stm, 1, obj.getKey());
			}

			public void executeStep(Connection cnn, C obj) throws SQLException {
			}
		};
	}


	@Override
	protected GetQueryCallback<C> getUpdateQueryCB() {
		return new GetQueryCallback<C>() {
			public String getSql() throws ORMException {
				StringBuilder attrs = new StringBuilder();
				if (attributes != null) {
					for (SqlAttribute a : attributes) {
						attrs.append(", " + a.getColumn() + " = ?");
					}
				}
				return "UPDATE " + getTableName() + " SET " + nameColumn + " = ? "
						+ attrs + " WHERE NVL(" + idColumn + ", '-') = NVL(?, '-')";
			}
	
			public void setParams(PreparedStatement stm, C obj)
					throws SQLException {
				stm.setString(1, obj.getName());
				int i = 0;
				if (attributes != null) {
					for (SqlAttribute a : attributes) {
						stm.setString(2 + i, obj.getAttribute(a.getCaption()));
						i++;
					}
				}
				setKeyValue(stm, 3 + i, obj.getKey());
			}

			public void executeStep(Connection cnn, C obj) throws SQLException {
			}
		};
	}
}
