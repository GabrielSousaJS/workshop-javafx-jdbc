package model.dao.impl;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SellerDaoJDBC implements SellerDao {

    private Connection conn;

    public SellerDaoJDBC(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void insert(Seller obj) {

        // Como não uma inserção e não uma busca de dados, não será necessário o ResultSet.
        PreparedStatement st = null;

        try {
            // Ao final do comando SQL é utilizado o Statment.RETURN_GENERATED_KEYS, para que o st possua a chave
            // primária da nova tupla.
            st = conn.prepareStatement("insert into seller (Name, Email, BirthDate, BaseSalary, DepartmentId) values (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

            // Após o objeto st está pronto, basta inserir os dados a partir do objeto Seller.
            st.setString(1, obj.getName());
            st.setString(2, obj.getEmail());
            // Como o padrão de data é SQL, é preciso utilizar o pacote Date do sql, posteriormente utilizar o getTime
            // para que não haja erros de tipo
            st.setDate(3, new Date(obj.getBirthDate().getTime()));
            st.setDouble(4, obj.getBaseSalaray());
            st.setInt(5, obj.getDepartment().getId());

            // Posteriormente irá verificar quantas linhas foram afetadas.
            int rowsAffected = st.executeUpdate();

            // Irá verificar se ouve linhas afetadas, caso o resultado seja negativo, irá propagar uma exceção.
            if (rowsAffected > 0) {
                // A chave primária gerada a partir do novo objeto é atribuido ao resultSet
                ResultSet rs = st.getGeneratedKeys();
                // Como é apenas uma linha, pois se trata de um insert. É utilizado o if e não o while.
                if (rs.next()) {
                    // Como o ResultSet está apenas com a chave primária da nova tupla, ela é a primeira coluna.
                    // Está parte é utilizada para que de imediato o objeto receba sua chave primária, não ficando
                    // desconhecida.
                    int id = rs.getInt(1);
                    obj.setId(id);
                }
                DB.closeResultSet(rs);
            } else {
                throw new DbException("Unexpected error! No rows affected!");
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
        }

    }

    @Override
    public void update(Seller obj) {
        PreparedStatement st = null;

        try {
            st = conn.prepareStatement("update seller set Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? where Id = ?");

            st.setString(1, obj.getName());
            st.setString(2, obj.getEmail());
            st.setDate(3, new Date(obj.getBirthDate().getTime()));
            st.setDouble(4, obj.getBaseSalaray());
            st.setInt(5, obj.getDepartment().getId());
            st.setInt(6, obj.getId());

            // Para realizar o update, não é necessário fazer a verificação detalhada.
            // Pois como é para atualizar, todos os dados estão preenchidos, não é como no insert em que o objeto não
            // possui a chave primária.
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
        }

    }

    @Override
    public void deleteById(Integer id) {
        PreparedStatement st = null;

        try {
            st = conn.prepareStatement("delete from seller where Id = ?");

            st.setInt(1, id);

            int rowsAffected = st.executeUpdate();

            if (rowsAffected == 0) {
                throw new DbException("Error! Id does not exist in the database.");
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
        }
    }

    @Override
    public Seller findById(Integer id) {

        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = conn.prepareStatement("select seller.*, department.Name as DepName from seller inner join department on seller.DepartmentId = department.Id where seller.Id = ?");
            st.setInt(1, id);
            rs = st.executeQuery();

            if (rs.next()) {
                Department dep = instantiateDepartment(rs);
                Seller obj = instantiateSeller(rs, dep);

                return obj;
            }

            return null;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }

    }

    private Seller instantiateSeller(ResultSet rs, Department dep) throws SQLException {
        Seller obj = new Seller();
        obj.setId(rs.getInt("Id"));
        obj.setName(rs.getString("Name"));
        obj.setEmail(rs.getString("Email"));
        obj.setBaseSalaray(rs.getDouble("BaseSalary"));
        obj.setBirthDate(rs.getDate("BirthDate"));
        obj.setDepartment(dep);

        return obj;
    }

    private Department instantiateDepartment(ResultSet rs) throws SQLException {
        Department dep = new Department();
        dep.setId(rs.getInt("Id"));
        dep.setName(rs.getString("Name"));

        return dep;
    }

    @Override
    public List<Seller> findAll() {
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = conn.prepareStatement("select seller.*, department.Name as DepName from seller inner join department on seller.DepartmentId = department.Id order by Name");
            rs = st.executeQuery();
            List<Seller> list = new ArrayList<>();
            Map<Integer, Department> map = new HashMap<>();

            while (rs.next()) {
                Department dep = map.get(rs.getInt("DepartmentId"));

                if (dep == null) {
                    dep = instantiateDepartment(rs);
                    map.put(rs.getInt("DepartmentId"), dep);
                }

                Seller obj = instantiateSeller(rs, dep);

                list.add(obj);
            }

            return list;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }

    }

    @Override
    public List<Seller> findByDepartment(Department department) {
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = conn.prepareStatement("select seller.*, department.Name as DepName from seller inner join department on seller.DepartmentId = department.id where DepartmentId = ? order by Name");
            st.setInt(1, department.getId());
            rs = st.executeQuery();

            List<Seller> list = new ArrayList<>();
            Map<Integer, Department> map = new HashMap<>();

            while (rs.next()) {
                // Irá ser guardado dentro do método todo o departamento que for instanciado.
                // Será pesquisado todos os departamentos que estão presentes.
                Department dep = map.get(rs.getInt("DepartmentId"));

                // Caso não seja null, quer dizer que sim tem pessoas.
                if (dep == null) {
                    dep = instantiateDepartment(rs);
                    map.put(rs.getInt("DepartmentId"), dep);
                }


                Seller obj = instantiateSeller(rs, dep);

                list.add(obj);
            }

            return list;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }
}
