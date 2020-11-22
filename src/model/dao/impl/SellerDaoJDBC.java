package model.dao.impl;

import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class SellerDaoJDBC implements SellerDao {

   private Connection connection;

   public SellerDaoJDBC(Connection connection){
       this.connection = connection;
   }

    @Override
    public void insert(Seller seller) {

       PreparedStatement st = null;
       try{

           st = connection.prepareStatement("INSERT INTO seller "
                   + "(Name, Email, BirthDate, BaseSalary, DepartmentId) "
                   + "VALUES "
                   + "(?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

           SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
           st.setString(1, seller.getName());
           st.setString(2, seller.getEmail());
           st.setDate(3, new java.sql.Date(seller.getBirthDate().getTime()));
           st.setDouble(4, seller.getBaseSalary());
           st.setInt(5, seller.getDepartment().getId());

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
    public void update(Seller seller) {


        PreparedStatement st = null;
        try{

            st = connection.prepareStatement("UPDATE seller "
                    + "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? "
                    + "WHERE Id = ?", Statement.RETURN_GENERATED_KEYS);

            st.setString(1, seller.getName());
            st.setString(2, seller.getEmail());
            st.setDate(3, new java.sql.Date(seller.getBirthDate().getTime()));
            st.setDouble(4, seller.getBaseSalary());
            st.setInt(5, seller.getDepartment().getId());
            st.setInt(6, seller.getId());

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
           PreparedStatement st = connection.prepareStatement("DELETE FROM seller " +
                                            "WHERE Id = ?");

           st.setInt(1, id);
           st.executeUpdate();
       }
       catch (SQLException e){
           throw new DbException(e.getMessage());
       }
    }

    @Override
    public Seller findById(Integer id) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try{
            st = connection.prepareStatement("SELECT seller.*,department.Name as DepName "
                    + "FROM seller INNER JOIN department "
                    + "ON seller.DepartmentId = department.Id "
                    + "WHERE seller.Id = ?");

            st.setInt(1, id);
            rs = st.executeQuery();
            if (rs.next()){
                return instantiateSeller(rs);
            }
            return null;
        }
        catch (SQLException e){
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public List<Seller> findAll() {
        PreparedStatement st = null;
        ResultSet rs = null;
        List<Seller> sellers = new ArrayList<>();
        try{
            st = connection.prepareStatement("SELECT seller.*,department.Name as DepName " +
                    "FROM seller INNER JOIN department " +
                    "ON seller.DepartmentId = department.Id " +
                    "ORDER BY Name");

            rs = st.executeQuery();

            while (rs.next()){
                sellers.add(instantiateSeller(rs));
            }
            return sellers;
        }
        catch (SQLException e){
            throw new DbException(e.getMessage());
        }

    }

    public List<Seller> findByDepartment (Department department){
        PreparedStatement st = null;
        ResultSet rs = null;
        List<Seller> sellers = new ArrayList<>();
        try{
            st = connection.prepareStatement("SELECT seller.*,department.Name as DepName " +
                    "FROM seller INNER JOIN department " +
                    "ON seller.DepartmentId = department.Id " +
                    "WHERE DepartmentId = ? " +
                    "ORDER BY Name");

            st.setInt(1, department.getId());
            rs = st.executeQuery();

            while (rs.next()){
                sellers.add(instantiateSeller(rs, department));
            }
            return sellers;
        }
        catch (SQLException e){
            throw new DbException(e.getMessage());
        }

    }

    private Seller instantiateSeller(ResultSet rs) throws SQLException{
        return new Seller(rs.getInt("Id"),
                rs.getString("Name"),
                rs.getString("Email"),
                rs.getDate("birthDate"),
                rs.getDouble("baseSalary"),
                new Department(rs.getInt("departmentId"), rs.getString("depName")));
    }

    private Seller instantiateSeller(ResultSet rs, Department department) throws SQLException{
        return new Seller(rs.getInt("Id"),
                rs.getString("Name"),
                rs.getString("Email"),
                rs.getDate("birthDate"),
                rs.getDouble("baseSalary"),
                department);
    }

}
