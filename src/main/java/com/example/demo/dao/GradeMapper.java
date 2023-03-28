package com.example.demo.dao;
import com.example.demo.entity.Grade;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import java.util.List;
/**
 * @description gradeMapper
 * @author gzy
 * @date 2023-01-12
 */
@Mapper
@Repository
public interface GradeMapper {

    @Select("select * from grade where grade_id=#{id}")
    public Grade getById(Integer id);

    @Select("select * from grade where grade!=0")
    public List<Grade> loadAllTrueGrade();

    @Options(useGeneratedKeys=true,keyProperty="gradeId")
    @Insert("insert into grade" +
            " (str,count,grade)" +
            " values(str,count,grade)")
    public Integer insert(Grade grade);

    @Delete(value = "delete from grade where grade_id=#{gradeId}")
    boolean delete(Integer id);

    @Update(value = "update grade set "
            +" str=#{str},"
            +" count=#{count},"
            +" grade=#{grade}"
            +" where grade_id=#{gradeId} ")
    boolean update(Grade grade);


    @Results(value = {
            @Result(property = "str", column = "str"),
            @Result(property = "count", column = "count"),
            @Result(property = "grade", column = "grade")
    })
    @Select(value = "select * from grade where grade_id=#{queryParam}")
    Grade selectOne(String queryParam);

    @Results(value = {
            @Result(property = "str", column = "str"),
            @Result(property = "count", column = "count"),
            @Result(property = "grade", column = "grade")
    })
    @Select(value = "select * from grade where "
            +" str=#{str} or "
            +" count=#{count} or "
            +" grade=#{grade}"
    )
    List<Grade> selectList(Grade grade);

    @Results(value = {
            @Result(property = "str", column = "str"),
            @Result(property = "count", column = "count"),
            @Result(property = "grade", column = "grade")
    })
    @Select(value = "select * from grade")
    List<Grade> selectAll();
}