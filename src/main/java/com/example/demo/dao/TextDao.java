package com.example.demo.dao;

import com.example.demo.component.TextPath;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface TextDao {
  @Select({"select * from pixiv_text_path "})
  List<TextPath> getAllTextPath();
  
  @Insert({"insert into pixiv_text_path(tid,path) values (#{tid}, #{path})"})
  int addTextPath(TextPath paramTextPath);
  
  @Delete({"delete from pixiv_text_path where id=#{id}"})
  int deleteTextPathById(int paramInt);
  
  @Update({"update pixiv_text_path set tid=#{tid},path=#{path}  where id=#{id}"})
  int updateTextPath(TextPath paramTextPath);
}


/* Location:              C:\Users\gzy\.m2\repository\com\example\textQuery\0.0.1-SNAPSHOT\textQuery-0.0.1-SNAPSHOT.jar!\BOOT-INF\classes\com\example\demo\dao\TextDao.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */