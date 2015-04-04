package com.ies.schoolos.container;

import java.io.Serializable;
import java.sql.SQLException;

import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.query.FreeformQuery;

public class FreeFormContainerService implements Serializable {
	private static final long serialVersionUID = 1L;

	private String sql;
	private String primaryKey;
	
	private SQLContainer container;
	
	public FreeFormContainerService(String sql, String primaryKey) {
		this.sql = sql;
		this.primaryKey = primaryKey;
		initContainer();
	}
	
	private void initContainer() {
        try {
        	FreeformQuery tq = new FreeformQuery(sql, DbConnection.getConnection(),primaryKey);
            container = new SQLContainer(tq);
            container.setAutoCommit(true);
        } catch (SQLException e) {
        	rollback();
            e.printStackTrace();
        }
    }
	
	public SQLContainer getContainer(){
		return container;
	}
	
	public void rollback(){
		try {
			container.rollback();
		} catch (UnsupportedOperationException | SQLException e) {
			e.printStackTrace();
		}
	}
	
}
