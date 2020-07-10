package org.apache.iotdb.client;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.apache.iotdb.service.rpc.thrift.IoTDBDataType;
import org.apache.iotdb.session.IoTDBRowBatch;
import org.apache.iotdb.session.Session;

public class SessionTest {

  public static void main(String[] args) throws ClassNotFoundException, SQLException {

    Class.forName("org.apache.iotdb.jdbc.IoTDBDriver");
    Connection connection = null;
    try {
      connection = DriverManager.getConnection("jdbc:iotdb://127.0.0.1:6667/", "root", "root");
      Statement statement = connection.createStatement();
      statement.execute("SET STORAGE GROUP TO root.sg1");
      statement.execute("CREATE TIMESERIES root.sg1.d1.s1 WITH DATATYPE=FLOAT, ENCODING=RLE");
      statement.execute("CREATE TIMESERIES root.sg1.d1.s2 WITH DATATYPE=FLOAT, ENCODING=RLE");
      statement.execute("CREATE TIMESERIES root.sg1.d1.s3 WITH DATATYPE=FLOAT, ENCODING=RLE");

      Session session = new Session("127.0.0.1", 6667, "root", "root");
      session.open();
      List<String> measurements = new ArrayList<>();
      measurements.add("s1");
      measurements.add("s2");
      measurements.add("s3");
      List<IoTDBDataType> dataTypes = new ArrayList<>();
      dataTypes.add(IoTDBDataType.FLOAT);
      dataTypes.add(IoTDBDataType.FLOAT);
      dataTypes.add(IoTDBDataType.FLOAT);

      List<Object> values = new ArrayList<>();
      values.add(1.0f);
      values.add(1.0f);
      values.add(1.0f);

      long start = System.currentTimeMillis();
      for (long i = 0; i < 10000; i++) {
        IoTDBRowBatch rowBatch = new IoTDBRowBatch("root.sg1.d1", measurements, dataTypes);
        for (long j = 0; j < 1000; j++) {
          rowBatch.addRow(i * 1000 + j, values);
        }
        session.insertBatch(rowBatch);
      }

      System.out.println("cost: " + (System.currentTimeMillis() - start));

      session.close();

//      ResultSet resultSet = statement.executeQuery("select * from root");
//      ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
//      while (resultSet.next()) {
//        StringBuilder builder = new StringBuilder();
//        for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
//          builder.append(resultSet.getString(i)).append(",");
//        }
//        System.out.println(builder);
//      }
      statement.close();

    } finally {
      connection.close();
    }



  }

}
