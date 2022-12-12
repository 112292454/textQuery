/*    */ package com.example.demo.utils;
/*    */ 
/*    */ import java.security.MessageDigest;
/*    */ import java.util.Random;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ShortUrlUtil
/*    */ {
/*    */   public String shortUrl(String url) {
/* 11 */     String key = "test";
/*    */     
/* 13 */     String[] chars = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 22 */     String hex = md5ByHex(key + url);
/*    */     
/* 24 */     String[] resUrl = new String[4];
/* 25 */     for (int i = 0; i < 4; i++) {
/*    */ 
/*    */       
/* 28 */       String sTempSubString = hex.substring(i * 8, i * 8 + 8);
/*    */ 
/*    */       
/* 31 */       long lHexLong = 0x3FFFFFFFL & Long.parseLong(sTempSubString, 16);
/* 32 */       String outChars = "";
/* 33 */       for (int j = 0; j < 6; j++) {
/*    */         
/* 35 */         long index = 0x3DL & lHexLong;
/*    */         
/* 37 */         outChars = outChars + chars[(int)index];
/*    */         
/* 39 */         lHexLong >>= 5L;
/*    */       } 
/*    */       
/* 42 */       resUrl[i] = outChars;
/*    */     } 
/* 44 */     return resUrl[(new Random()).nextInt(4)];
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String md5ByHex(String src) {
/*    */     try {
/* 53 */       MessageDigest md = MessageDigest.getInstance("MD5");
/* 54 */       byte[] b = src.getBytes();
/* 55 */       md.reset();
/* 56 */       md.update(b);
/* 57 */       byte[] hash = md.digest();
/* 58 */       String hs = "";
/* 59 */       String stmp = "";
/* 60 */       for (int i = 0; i < hash.length; i++) {
/* 61 */         stmp = Integer.toHexString(hash[i] & 0xFF);
/* 62 */         if (stmp.length() == 1) {
/* 63 */           hs = hs + "0" + stmp;
/*    */         } else {
/* 65 */           hs = hs + stmp;
/*    */         } 
/*    */       } 
/* 68 */       return hs.toUpperCase();
/* 69 */     } catch (Exception e) {
/* 70 */       return "";
/*    */     } 
/*    */   }
/*    */ }


/* Location:              D:\Spring\mvn_repository\com\example\textQuery\0.0.1-SNAPSHOT\textQuery-0.0.1-SNAPSHOT.jar!\BOOT-INF\classes\com\example\dem\\utils\ShortUrlUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */