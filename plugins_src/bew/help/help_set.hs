<?xml version="1.0" encoding='ISO-8859-1' ?>
<!DOCTYPE helpset
PUBLIC "-//Sun Microsystems Inc.//DTD JavaHelp HelpSet Version 1.0//EN"
"http://java.sun.com/products/javahelp/helpset_1_0.dtd">
<helpset version="2.0">
   <title>BEW's help menu</title>
   <maps>
      <!-- Default page -->
      <homeID>index</homeID>
      <!-- Map to use -->
      <mapref location="map_file.xml"/>
   </maps> 

   <!-- Our Views -->
   <!-- Content Table, Toc File --> 
   <view>
      <name>Contents Table</name>
      <label>Contents Table</label>
      <type>javax.help.TOCView</type>
      <data>toc.xml</data>
   </view> 

   <!-- Index File --> 
   <view>
      <name>Index</name>
      <label>Index</label>
      <type>javax.help.IndexView</type>
      <data>index.xml</data>
   </view>

   <!-- Search File --> 
   <view>
      <name>Search</name>
      <label>Search</label>
      <type>javax.help.SearchView</type>
      <data engine="com.sun.java.help.search.DefaultSearchEngine">
         JavaHelpSearch
      </data>
   </view>
</helpset>