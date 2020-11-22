package model.dao.impl;

import db.DbException;
import model.dao.DepartmentDao;
import model.entities.Department;
import model.entities.Seller;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDaoJDBC implements DepartmentDao {
    
    private Connection connection;

    public DepartmentDaoJDBC(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void insert(Department department) {
        try{

            PreparedStatement st = connection.prepareStatement("INSERT INTO department "
                    + "(Name, Id) "
                    + "VALUES "
                    + "(?, ?)", Statement.RETURN_GENERATED_KEYS);
            
            st.setString(1, department.getName());
           
            st.setInt(2, department.getId());

            int rowsAffected = st.executeUpdate();
            if (rowsAffected == 0){
                throw new DbException("An error has ocurred!");
            }
        }
        catch (SQLException e){
            throw new DbException(e.getMessage());
        }
        
    }

    @Override
    public void update(Department department) {

        PreparedStatement st = null;
        try{

            st = connection.prepareStatement("UPDATE department "
                    + "SET Name = ?, Id = ? "
                    + "WHERE Id = ?", Statement.RETURN_GENERATED_KEYS);

            st.setString(1, department.getName());
            st.setInt(2, department.getId());
            st.setInt(3, department.getId());

            int rowsAffected = st.executeUpdate();
            if (rowsAffected == 0){
                throw new DbException("An error has ocurred!");
            }
        }
        catch (SQLException e){
            throw new DbException(e.getMessage());
        }

    }

    @Override
    public void deleteById(Integer id) {

        try{
            PreparedStatement st = connection.prepareStatement("DELETE FROM department " +
                    "WHERE Id = ?");

            st.setInt(1, id);
            st.executeUpdate();
        }
        catch (SQLException e){
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public Department findById(Integer id) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try{
            st = connection.prepareStatement("SELECT * "
                    + "FROM department "
                    + "WHERE department.Id = ?");

            st.setInt(1, id);
            rs = st.executeQuery();
            if (rs.next()){
                return instantiateDepartment(rs);
            }
            return null;
        }
        catch (SQLException e){
            throw new DbException(e.getMessage());
        }
    }



    @Override
    public List<Department> findAll() {
        PreparedStatement st = null;
        ResultSet rs = null;
        List<Department> departments = new ArrayList<>();
        try{
            st = connection.prepareStatement("SELECT * " +
                    "FROM department ");

            rs = st.executeQuery();

            while (rs.next()){
                departments.add(instantiateDepartment(rs));
            }
            return departments;
        }
        catch (SQLException e){
            throw new DbException(e.getMessage());
        }

    }

    private Department instantiateDepartment(ResultSet rs) throws SQLException{
        return new Department(rs.getInt("Id"), rs.getString("Name"));
    }
}
