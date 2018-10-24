package org.sang.TEST;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.sang.bean.Employee;
import org.sang.bean.Nation;
import org.sang.bean.PoliticsStatus;
import org.sang.service.EmpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.List;

/**
 * @Author: CarryJey @Date: 2018/10/24 16:25:06
 */
@Component
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestEmpService {

    @Autowired
    private EmpService empService;


    @Test
    public void testAddEmp(){
        Employee employee = empService.getEmpById(1);
        empService.addEmp(employee);
    }

    @Test
    public void testGetAllNations(){
        List<Nation> res= empService.getAllNations();
        for(Nation r : res){
            System.out.println(r.getId() + " : " +r.getName());
        }
    }

    @Test
    public void testGetAllPolitics(){
        List<PoliticsStatus> res = empService.getAllPolitics();
        for(PoliticsStatus r : res){
            System.out.println(r.getId() + " : " +r.getName());
        }
    }

    @Test
    public void getMaxWorkId(){
        Long max = empService.getMaxWorkID();
        System.out.println(max);
    }


    @Test
    public void testDeletById(){
        //有外键，删除会出错
        String id = "2";
        empService.deleteEmpById(id);
    }

    @Test
    public void testGetEmployeeByPageShort(){
        List<Employee> res = empService.getEmployeeByPageShort(0,15);
        for(Employee r : res){
            System.out.println(r.getName());
        }
    }

    @Test
    public void testGetEmpByPage(){
        List<Employee> res = empService.getEmployeeByPage(0,15,"11",1L,1L,1L,1L,"fk",1L,"1");
        for(Employee r : res){
            System.out.println(r.getName());
        }
    }
    //语法错误接口
//        empService.getEmployeeByPage();
//        empService.getEmployeeByPageShort();
//        empService.getCountByKeywords();


}
