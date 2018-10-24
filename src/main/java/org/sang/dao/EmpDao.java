package org.sang.dao;

import org.sang.bean.Employee;
import org.sang.bean.Nation;
import org.sang.bean.PoliticsStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @Author: CarryJey @Date: 2018/10/24 14:27:32
 */
@Repository
public class EmpDao {

    private static final BeanPropertyRowMapper<Employee> rowMapper = BeanPropertyRowMapper.newInstance(Employee.class);
    private static final BeanPropertyRowMapper<Nation> rowMapper2 = BeanPropertyRowMapper.newInstance(Nation.class);
    private static final BeanPropertyRowMapper<PoliticsStatus> rowMapper3 =
        BeanPropertyRowMapper.newInstance(PoliticsStatus.class);

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public List<Nation> getAllNations() {
        String sql = "select * from nation";
        List<Nation> res = jdbcTemplate.query(sql, rowMapper2);
        if (res.size() > 0) {
            return res;
        }
        return null;
    }

    public List<PoliticsStatus> getAllPolitics() {
        String sql = "select * from politicsstatus";
        List<PoliticsStatus> res = jdbcTemplate.query(sql, rowMapper3);
        if (res.size() > 0) {
            return res;
        }
        return null;
    }

    public Employee getEmpById(int id) {
        String sql = "SELECT * from employee where id = :id";
        List<Employee> res = jdbcTemplate.query(sql, new MapSqlParameterSource("id", id), rowMapper);
        if (res.size() > 0) {
            return res.get(0);
        }
        return null;
    }

    public int addEmp(Employee employee) {
        String sql =
            "insert into employee (name, gender,"
                + "birthday, idCard, wedlock, nationId,"
                + "nativePlace, politicId, email,"
                + "phone, address, departmentId,"
                + "jobLevelId, posId, engageForm,"
                + "tiptopDegree, specialty, school,"
                + "beginDate,workID,"
                + "contractTerm, conversionTime,"
                + "beginContract, endContract, workAge) "
                + " values (:name, :gender,"
                + ":birthday, :idCard, :wedlock, :nationId,"
                + ":nativePlace, :politicId, :email,"
                + ":phone, :address, :departmentId,"
                + ":jobLevelId, :posId, :engageForm,"
                + ":tiptopDegree, :specialty, :school,"
                + ":beginDate,:workID,"
                + ":contractTerm, :conversionTime,"
                + ":beginContract, :endContract, :workAge)";
        int rows = jdbcTemplate.update(sql, new BeanPropertySqlParameterSource(employee));
        assert rows == 1;
        return rows;
    }

    public Long getMaxWorkID() {
        String sql = " SELECT workID from employee where id=(select max(id) from employee)";
        List<Employee> res = jdbcTemplate.query(sql, rowMapper);
        if (res.size() > 0) {
            return Long.valueOf(res.get(0).getWorkID());
        }
        return null;
    }

    public List<Employee> getEmployeeByPage(
        Integer start,
        Integer size,
        String keywords,
        Long politicId,
        Long nationId,
        Long posId,
        Long jobLevelId,
        String engageForm,
        Long departmentId,
        Date startBeginDate,
        Date endBeginDate) {

        StringBuilder sql =
            new StringBuilder(
                "select e.*,jl.`id` as jlid,jl.`name` as jlname,jl.`titleLevel` as jlTitleLevel,d.`id` as did,d.`name` as"
                    + " dname,n.`id` as nid,n.`name` as nname,p.`id` as pid,p.`name` as pname,ps.`id` as psid,ps.`name` as psname "
                    + " from employee e,joblevel jl,department d,nation n,position p,politicsstatus ps "
                    + " where e.`posId`=p.`id` and e.`jobLevelId`=jl.`id` and e.`departmentId`=d.id and e.`nationId`=n.`id` "
                    + " and e.`politicId`=ps.`id` and e.`name`"
                    + " like concat('%',:keywords,'%')");
        MapSqlParameterSource param = new MapSqlParameterSource("keywords", keywords);
        if (!engageForm.isEmpty()) {
            sql.append(" AND e.engageForm = :engageForm ");
            param.addValue("engageForm", engageForm);
        }
        if (politicId != null) {
            sql.append(" AND e.politicId = :politicId ");
            param.addValue("politicId", politicId);
        }
        if (nationId != null) {
            sql.append(" AND e.nationId = :nationId ");
            param.addValue("nationId", nationId);
        }
        if (posId != null) {
            sql.append(" AND e.posId = :posId ");
            param.addValue("posId", posId);
        }
        if (jobLevelId != null) {
            sql.append(" AND e.jobLevelId = :jobLevelId ");
            param.addValue("jobLevelId", jobLevelId);
        }
        if (departmentId != null) {
            sql.append(" AND e.departmentId = :departmentId ");
            param.addValue("departmentId", departmentId);
        }
        if (startBeginDate != null && endBeginDate != null) {
            sql.append(" AND e.startBeginDate BETWEEN  :startBeginDate AND :endBeginDate ");
            param.addValue("startBeginDate", startBeginDate).addValue("endBeginDate", endBeginDate);
        }
        if (start != null && size != null) {
            sql.append(" ORDER BY e.id LIMIT :start,:size ");
            param.addValue("start", start).addValue("size", size);
        }
        List<Employee> res = jdbcTemplate.query(sql.toString(), param, rowMapper);
        if (res.size() > 0) {
            return res;
        }
        return null;
    }

    public Long getCountByKeywords(
        String keywords,
        Long politicId,
        Long nationId,
        Long posId,
        Long jobLevelId,
        String engageForm,
        Long departmentId,
        Date startBeginDate,
        Date endBeginDate) {
        StringBuilder sql =
            new StringBuilder(" SELECT count(*) FROM employee e WHERE e.name LIKE concat('%',:keywords,'%') ");
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("keywords", keywords);
        if (!engageForm.isEmpty()) {
            sql.append(" AND e.engageForm = :engageForm ");
            param.addValue("engageForm", engageForm);
        }
        if (politicId != null) {
            sql.append(" AND e.politicId = :politicId");
            param.addValue("politicId", politicId);
        }
        if (nationId != null) {
            sql.append(" AND e.nationId = :nationId");
            param.addValue("nationId", nationId);
        }
        if (posId != null) {
            sql.append(" AND e.posId = :posId");
            param.addValue("posId", posId);
        }
        if (jobLevelId != null) {
            sql.append(" AND e.jobLevelId = :jobLevelId");
            param.addValue("jobLevelId", jobLevelId);
        }
        if (departmentId != null) {
            sql.append(" AND e.departmentId = :departmentId");
            param.addValue("departmentId", departmentId);
        }
        if (startBeginDate != null) {
            sql.append(" AND e.startBeginDate = :startBeginDate");
            param.addValue("startBeginDate", startBeginDate);
        }

        int res = jdbcTemplate.queryForObject(sql.toString(), EmptySqlParameterSource.INSTANCE, Integer.class);

        return Long.valueOf(res);
    }

    public int updateEmp(Employee employee) {
        StringBuilder sql = new StringBuilder("UPDATE employee SET ");
        if (!employee.getName().isEmpty()) {
            sql.append(" name = :name ");
        }
        if (!employee.getGender().isEmpty()) {
            sql.append(" gender = :gender ");
        }
        if (employee.getIdCard() != null) {
            sql.append(" idCard = :idCard ");
        }
        if (employee.getWedlock() != null) {
            sql.append(" wedlock = :wedlock ");
        }
        if (employee.getNationId() != null) {
            sql.append(" nationId = :nationId ");
        }
        if (employee.getNativePlace() != null) {
            sql.append(" nativePlace = :nativePlace ");
        }
        if (employee.getPoliticId() != null) {
            sql.append(" politicId = :politicId ");
        }
        if (employee.getEmail() != null) {
            sql.append(" email = :email ");
        }
        if (employee.getPhone() != null) {
            sql.append(" phone = :phone ");
        }
        if (employee.getAddress() != null) {
            sql.append(" address = :address ");
        }
        if (employee.getDepartmentId() != null) {
            sql.append(" departmentId = :departmentId ");
        }
        if (employee.getJobLevelId() != null) {
            sql.append(" jobLevelId = :jobLevelId ");
        }
        if (employee.getPosId() != null) {
            sql.append(" posId = :posId ");
        }
        if (employee.getEngageForm() != null) {
            sql.append(" engageForm = :engageForm ");
        }
        if (employee.getTiptopDegree() != null) {
            sql.append(" tiptopDegree = :tiptopDegree ");
        }
        if (employee.getSpecialty() != null) {
            sql.append(" specialty = :specialty ");
        }
        if (employee.getSchool() != null) {
            sql.append(" school = :school ");
        }
        if (employee.getBeginDate() != null) {
            sql.append(" beginDate = :beginDate ");
        }
        if (employee.getSpecialty() != null) {
            sql.append(" specialty = :specialty ");
        }
        if (employee.getWorkState() != null) {
            sql.append(" workState = :workState ");
        }
        if (employee.getContractTerm() != null) {
            sql.append(" contractTerm = :contractTerm ");
        }
        if (employee.getConversionTime() != null) {
            sql.append(" conversionTime = :conversionTime ");
        }
        if (employee.getNotWorkDate() != null) {
            sql.append(" notWorkDate = :notWorkDate ");
        }
        if (employee.getBeginContract() != null) {
            sql.append(" beginContract = :beginContract ");
        }
        if (employee.getEndContract() != null) {
            sql.append(" endContract = :endContract ");
        }

        sql.append("WHERE id = :id");
        int rows = jdbcTemplate.update(sql.toString(), new BeanPropertySqlParameterSource(employee));
        assert rows == 1;
        return rows;
    }

    public int deleteEmpById(String[] ids) {
        String sql;
        int res = 1;
        for (String id : ids) {
            sql = " DELETE FROM employee WHERE id = :id";
            int rows = jdbcTemplate.update(sql, new MapSqlParameterSource("id", id));
            if (rows == 0) {
                res = 0;
            }
        }
        return res;
    }

    public int addEmps(List<Employee> emps) {
        String sql;
        int res = 1;
        for (Employee emp : emps) {
            sql =
                " insert into employee (name, gender,"
                    + "birthday, idCard, wedlock, nationId,"
                    + "nativePlace, politicId, email,"
                    + "phone, address, departmentId,"
                    + "jobLevelId, posId, engageForm,"
                    + "tiptopDegree, specialty, school,"
                    + "beginDate,workID,"
                    + "contractTerm, conversionTime,"
                    + "beginContract, endContract, workAge) "
                    + " VALUES(:name, :gender,"
                    + ":birthday, :idCard, :wedlock, :nationId,"
                    + ":nativePlace, :politicId, :email,"
                    + ":phone, :address, :departmentId,"
                    + ":jobLevelId, :posId, :engageForm,"
                    + ":tiptopDegree, :specialty, :school,"
                    + ":beginDate,:workID,"
                    + ":contractTerm, :conversionTime,"
                    + ":beginContract, :endContract, :workAge)";
            int rows = jdbcTemplate.update(sql, new BeanPropertySqlParameterSource(emp));
            if (rows == 0) {
                res = 0;
            }
        }
        return res;
    }

    public List<Employee> getEmployeeByPageShort(int start, Integer size) {
        String sql =
            "select e.*,d.`id` as did,d.`name` as dname,s.`id` as sid,s.`accumulationFundBase`,s.`accumulationFundPer`,"
                + "s.`allSalary`,s.`basicSalary`,s.`bonus`,s.`createDate`,s.`lunchSalary`,s.`medicalBase`,s.`medicalPer`,"
                + "s.`name` as sname,s.`pensionBase`,s.`pensionPer`,s.`trafficSalary` "
                + "from employee e "
                + "left join department d on e.`departmentId`=d.id "
                + "left join empsalary es on es.`eid`=e.`id` "
                + "left join salary s on s.`id`=es.`sid` "
                + "ORDER BY e.id limit :start,:size";
        List<Employee> res =
            jdbcTemplate.query(sql, new MapSqlParameterSource("start", start).addValue("size", size), rowMapper);
        return res;
    }
}
