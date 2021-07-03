package ru.szhernovoy.servlets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import ru.szhernovoy.dbase.DBManager;
import ru.szhernovoy.model.Item;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

/**
 * Created by Admin on 07.01.2017.
 */
public class Controller extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/json");
        DBManager manager = new DBManager();
        PrintWriter out = resp.getWriter();
        JsonObject result  = new JsonObject();
        Item task = new Item();
        task.setDesc(req.getParameter("descr"));
        task.setDone(Boolean.valueOf(req.getParameter("done")));
        result.addProperty("successCreate", manager.createTask(task));
        out.append(result.toString());
        out.flush();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/json;charset=Windows-1251");
        resp.setCharacterEncoding("UTF-8");
        boolean alltasks = Boolean.valueOf(req.getParameter("doneAll"));
        PrintWriter out = new PrintWriter(resp.getOutputStream());
        JsonObject jsonObject = new JsonObject();
        DBManager manager = new DBManager();
        jsonObject.addProperty("tasks", converterToJson(manager.getItems(alltasks)).toString());
        out.append(jsonObject.toString());
        out.flush();
    }

    public JsonArray converterToJson(Collection<Item> items){
        JsonArray array = new JsonArray();
        Item item = null;
        for (Item task : items) {
            JsonObject obj = new JsonObject();
            obj.addProperty("descr", task.getDesc());
            obj.addProperty("createDate", task.getCreate().toString());
            String done = "";
            if (task.getDone()) {
                done = "V";
            }
            obj.addProperty("done", done);
            array.add(obj);
        }
        return array;
    }


}
