package com.khronos.dao;

import com.khronos.db.Database;
import com.khronos.model.Task;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class TaskDAOImpl implements TaskDAO {


    private static final String BASE_SELECT =
            """
            SELECT 
                t.id,
                t.name,
                t.inicio,
                p.id AS project_id,
                p.name AS project_name,
                p.color AS project_color
            FROM tasks t
            JOIN projects p 
            ON p.id = t.project_id
            """;



    @Override
    public List<Task> findAll() throws SQLException {


        String sql =
                BASE_SELECT +
                        " ORDER BY t.id";


        List<Task> tasks =
                new ArrayList<>();


        try(
                Connection conn = Database.getConnection();
                PreparedStatement stmt =
                        conn.prepareStatement(sql);
                ResultSet rs =
                        stmt.executeQuery()
        ){


            while(rs.next()){

                tasks.add(map(rs));

            }

        }


        return tasks;

    }




    @Override
    public List<Task> findByProject(int projectId)
            throws SQLException {


        String sql =
                BASE_SELECT +
                        " WHERE p.id = ? ORDER BY t.id";


        List<Task> tasks =
                new ArrayList<>();



        try(
                Connection conn = Database.getConnection();
                PreparedStatement stmt =
                        conn.prepareStatement(sql)
        ){


            stmt.setInt(1, projectId);



            ResultSet rs =
                    stmt.executeQuery();



            while(rs.next()){

                tasks.add(map(rs));

            }

        }


        return tasks;

    }





    @Override
    public Task insert(String name, int projectId)
            throws SQLException {



        String sql =
                """
                INSERT INTO tasks
                (
                    name,
                    project_id,
                    inicio
                )
                VALUES
                (
                    ?,
                    ?,
                    CURRENT_TIMESTAMP
                )
                RETURNING id
                """;



        try(
                Connection conn =
                        Database.getConnection();

                PreparedStatement stmt =
                        conn.prepareStatement(sql)
        ){


            stmt.setString(1,name);

            stmt.setInt(2,projectId);



            ResultSet rs =
                    stmt.executeQuery();



            rs.next();



            return findById(
                    rs.getInt("id")
            );


        }

    }





    @Override
    public Task findById(int id)
            throws SQLException {


        String sql =
                BASE_SELECT +
                        " WHERE t.id = ?";



        try(
                Connection conn =
                        Database.getConnection();

                PreparedStatement stmt =
                        conn.prepareStatement(sql)
        ){


            stmt.setInt(1,id);



            ResultSet rs =
                    stmt.executeQuery();



            if(rs.next()){

                return map(rs);

            }


            return null;


        }


    }






    @Override
    public void update(Task task)
            throws SQLException {


        String sql =
                """
                UPDATE tasks
                SET
                    name = ?,
                    project_id = ?,
                    inicio = ?
                WHERE id = ?
                """;



        try(
                Connection conn =
                        Database.getConnection();

                PreparedStatement stmt =
                        conn.prepareStatement(sql)
        ){


            stmt.setString(
                    1,
                    task.getName()
            );


            stmt.setInt(
                    2,
                    task.getProjectId()
            );



            if(task.getStartedAt()!=null){

                stmt.setTimestamp(
                        3,
                        Timestamp.valueOf(
                                task.getStartedAt()
                        )
                );

            }else{

                stmt.setTimestamp(
                        3,
                        null
                );

            }



            stmt.setInt(
                    4,
                    task.getId()
            );



            stmt.executeUpdate();


        }


    }






    @Override
    public void delete(int id)
            throws SQLException {


        String sql =
                "DELETE FROM tasks WHERE id = ?";



        try(
                Connection conn =
                        Database.getConnection();

                PreparedStatement stmt =
                        conn.prepareStatement(sql)
        ){


            stmt.setInt(1,id);


            stmt.executeUpdate();


        }


    }







    private Task map(ResultSet rs)
            throws SQLException {



        Timestamp timestamp =
                rs.getTimestamp("inicio");



        LocalDateTime inicio =
                null;



        if(timestamp != null){

            inicio =
                    timestamp.toLocalDateTime();

        }




        return new Task(

                rs.getInt("id"),

                rs.getString("name"),

                rs.getInt("project_id"),

                rs.getString("project_name"),

                rs.getString("project_color"),

                inicio

        );


    }


}