package com.example.demo.dao;

import com.example.demo.entity.TextPath;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

@Mapper
public interface TextDao {

  //@Select({"select path from pixiv_text_path where tid=#{tid}"})
  TextPath getPathByTid(String tid);

  //@Select({"select * from pixiv_text_path where grade is null"})
  List<TextPath> tempGet();

  //@Select({"select tid,grade from pixiv_text_path where tid in (${TidList})"})
  //@Results(id="BaseResultMap", value={
  //        @Result(column="path", property="path", jdbcType= JdbcType.VARCHAR),
  //        @Result(column="tid", property="tid", jdbcType= JdbcType.VARCHAR),
  //        @Result(column="grade", property="grade", jdbcType=JdbcType.INTEGER),
  //})
  List<TextPath> getAllByTidS(@Param("TidList") String TidList);

  //@Select({"select path,tid,grade from pixiv_text_path order by grade desc limit #{k}"})
  //@ResultMap("BaseResultMap")
  List<TextPath> getTopK(@Param("k") Integer k);

  List<TextPath> getFavor();

  List<TextPath> getAll();

  //@Insert({"insert into pixiv_text_path(tid,path,raw_grade,grade) values (#{tid}, #{path}, #{raw_grade}, #{grade})"})
  int addTextPath(TextPath paramTextPath);

  //@Insert({"<script>\n" +
  //        "insert into pixiv_text_path(tid,path) values \n" +
  //        "           <foreach collection=\"list\"  item=\"item\" separator=\",\">\n" +
  //        "                (#{item.tid},#{item.path})\n" +
  //        "           </foreach>\n" +
  //        "</script>"})
  //int addTextPathBatch(ArrayList<TextPath> tran);
  
  //@Delete({"delete from pixiv_text_path where id=#{id}"})
  int deleteTextPathByTid(String tid);
  
  //@Update({"update pixiv_text_path set tid=#{tid},path=#{path}  where id=#{id}"})
  int updateTextPath(TextPath paramTextPath);

  boolean setFavor(String tid);
}


/* Location:              C:\Users\gzy\.m2\repository\com\example\textQuery\0.0.1-SNAPSHOT\textQuery-0.0.1-SNAPSHOT.jar!\BOOT-INF\classes\com\example\demo\dao\TextDao.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */