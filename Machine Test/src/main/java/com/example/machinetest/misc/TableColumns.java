package com.example.machinetest.misc;

import java.util.*;


public class TableColumns
{

    public static Map<String, String[]> getColumns(String[][] fileData)
    {
        Map<String, String[]> columns = new HashMap();
        String[] headers = fileData[0];
//        headers = {"First", "Last", "Mobile"};
        System.out.println("This is header "+ headers.toString());
        for(int rowIndex = 1; rowIndex < fileData.length; rowIndex++)
        {
            String[] row = fileData[rowIndex];
//            2ndRow = {"Sam", "Singh", "983710"},
            System.out.println("This is row " + row );
            for(int i = 0; i < row.length; i++)
            {
                String columnName = headers[i];
//                headers[0] = "First";
                System.out.println("this is column Name  " + columnName);
                if(!columns.containsKey(columnName))
                {
                    String[] s = new String[fileData.length - 1];
                    System.out.println("This is s "+ s );
                    columns.put(columnName, s);
//                    First = ["Sam", null, null]
                }

                String[] s = columns.get(columnName);

                s[rowIndex - 1] = row[i];
//                columns.get('First', String[3])
//                row[i] = "Sam"
//                First = ["Sam", "Joe", null]
//                Last = ["Singh", "Sharma", null]
//                Mobile = ["9893", null, null]
            };
        }
        return columns;
    }
    public static void main(String[] args)
    {

        String[][] fileData = {
                {"First", "Last", "Mobile"},
                {"Sam", "Singh", "983710"},
                {"Joe", "Sharma", "817230"},
                {"Veronica", "Devi", "710312"},
        };


        Map<String, String[]> dataByColumns = getColumns(fileData);
        for(String columnName:dataByColumns.keySet())
        {
            System.out.println(columnName);
            String[] rows = dataByColumns.get(columnName);
            for(String value: rows)
            {
                System.out.println(value);
            }
            System.out.println();
        }
    }
}
