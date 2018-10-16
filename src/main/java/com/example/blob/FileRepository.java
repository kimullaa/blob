package com.example.blob;

import org.apache.ibatis.annotations.*;

@Mapper
public interface FileRepository {

    @Select("SELECT id, name, file AS in FROM file WHERE id = #{id}")
    File findOne(@Param("id") String id);

    @Insert("INSERT INTO file (id, name, file) VALUES (#{file.id}, #{file.name}, #{file.in})")
    int save(@Param("file") File file);

    @Delete("DELETE FROM file WHERE id = #{id} AND ( SELECT lo_unlink( (SELECT file FROM file WHERE id = #{id})) ) = 1")
    int delete(@Param("id") String id);
}
