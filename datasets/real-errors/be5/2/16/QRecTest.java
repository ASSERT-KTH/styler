package com.developmentontheedge.be5.query.model.beans;

import com.developmentontheedge.be5.base.util.DateUtils;
import com.developmentontheedge.be5.query.QueryBe5ProjectDBTest;
import com.developmentontheedge.be5.query.services.QueriesService;
import com.developmentontheedge.beans.DynamicPropertySetSupport;
import org.junit.Test;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Collections;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class QRecTest extends QueryBe5ProjectDBTest
{
    @Inject QueriesService queries;

    @Test
    public void getDate()
    {
        DynamicPropertySetSupport dps = getDps(new DynamicPropertySetSupport(),
                Collections.singletonMap("test", DateUtils.makeDate(2018, 10, 27)));
        QRec qRec = new QRec(dps);
        assertEquals("2018-10-27", qRec.getDate().toString());
        assertEquals("2018-10-27", qRec.getDate("test").toString());
    }

    @Test
    public void commentTEXT() throws SQLException
    {
        db.update("delete from testSubQuery");
        db.insert("insert into testSubQuery (name, value, commentTEXT) VALUES (?, ?, ?)",
                "test", 1, "test2");
        QRec qRec = queries.queryRecord("select * from testSubQuery", Collections.emptyMap());

        assertEquals("test2", new BufferedReader(
                new InputStreamReader(qRec.getBinaryStream("commentTEXT"), StandardCharsets.UTF_8)).lines()
                .parallel().collect(Collectors.joining("")));
    }

    @Test
    public void commentBLOB() throws SQLException
    {
        db.update("delete from testSubQuery");
        InputStream test1 = new ByteArrayInputStream( "test1".getBytes() );

        db.insert("insert into testSubQuery (name, value, commentBLOB) VALUES (?, ?, ?)",
                "test", 1, test1);
        QRec qRec = queries.queryRecord("select * from testSubQuery", Collections.emptyMap());

//        assertEquals("test1", new BufferedReader(
//                new InputStreamReader(qRec.getBinaryStream("commentBLOB"))).lines()
//                .parallel().collect(Collectors.joining("")));
        assertEquals("test1", qRec.getString("commentBLOB"));
    }

    @Test
    public void getBinaryStream() throws SQLException
    {
        String example = "This is an example";
        byte[] bytes = example.getBytes();
        DynamicPropertySetSupport dps = getDps(new DynamicPropertySetSupport(),
                Collections.singletonMap("test", bytes));
        QRec qRec = new QRec(dps);
        assertEquals("This is an example", new BufferedReader(
                    new InputStreamReader(qRec.getBinaryStream(), StandardCharsets.UTF_8)).lines()
                .parallel().collect(Collectors.joining("")));
        assertEquals("This is an example", new BufferedReader(
                new InputStreamReader(qRec.getBinaryStream("test"), StandardCharsets.UTF_8)).lines()
                .parallel().collect(Collectors.joining("")));
    }
}
