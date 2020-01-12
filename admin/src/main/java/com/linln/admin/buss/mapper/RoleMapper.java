 package com.linln.admin.buss.mapper;

import org.apache.ibatis.annotations.Select;

public interface RoleMapper {

     @Select({
         "SELECT " + 
         "    `name` " + 
         "FROM " + 
         "    `sys_user_role` AS sur " + 
         "LEFT JOIN sys_role AS sr ON sr.id = sur.role_id " + 
         "WHERE " + 
         "    sur.user_id = #{userId}"
     })
     public String getRoleNameByUserId(Long userId);
}
