 package com.linln.admin.buss.util;

 import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

 @Data
 public class PageInfo<E> implements Serializable {

     private static final long serialVersionUID = 1L;

     public static final int PAGE_SIZE = 15;

     private int offset; // 内部使用的
     private int total;
     private List<E> items;

     private int pageNum; // 返回给前端使用的
     private int pageSize; // 返回给前端使用的

     public PageInfo(Integer pageNum, Integer pageSize) {
         this.pageNum = (pageNum == null || pageNum < 1 ? 1 : pageNum);
         this.pageSize = (pageSize == null || pageSize < 1 ? PAGE_SIZE : pageSize);
         this.offset = (this.pageNum - 1) * this.pageSize;
     }

     /**
      * <p>
      * Description: 正常分页使用此变量
      * </p>
      *
      * @return
      */
     public int getOffset() {
         if (this.offset >= this.total) {
             double total = 0 >= this.total ? 1 : this.total;
             double pageSize = this.pageSize;

             return ((int) Math.ceil(total / pageSize) - 1) * this.pageSize;
         }

         return this.offset;
     }

     /**
      * <p>
      * Description: 分页-加载更多时使用此变量
      * </p>
      * 
      * @author zhanghua
      * @return
      */
     public int getOrgOffset() {
         return this.offset;
     }

     public List<E> getItems() {
         if (items == null)
             items = new ArrayList<>();
         return items;
     }

     public static void main(String[] args) {
         double total = 1;
         double pageSize = 100000;
         System.out.println(Math.ceil(total / pageSize));
     }

 }
