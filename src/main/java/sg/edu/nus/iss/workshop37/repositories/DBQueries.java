package sg.edu.nus.iss.workshop37.repositories;

public class DBQueries {
    public static final String SQL_INSERT_IMAGE = """
            insert into images (filename, media_type, content) values (?, ?, ?)

                    """;
    public static final String SQL_SELECT_IMAGE = """
            select * from images where image_id = ?
            """;
}
