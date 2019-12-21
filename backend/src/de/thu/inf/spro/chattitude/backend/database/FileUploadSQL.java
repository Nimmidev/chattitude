package de.thu.inf.spro.chattitude.backend.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

final class FileUploadSQL extends BaseSQL {

    static final String TABLE_NAME = "FileUpload";
    static final String _ID = "fileId";
    static final String _DATA = "data";

    private static final String CREATE_TABLE = "" +
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ( " +
            _ID + " VARCHAR(36) NOT NULL PRIMARY KEY UNIQUE , " +
            _DATA + " LONGBLOB NOT NULL " +
            ");";

    private static final String ADD = "" + 
            "INSERT INTO " + TABLE_NAME + " (" + _ID + ", " + _DATA + ") VALUES (?, ?);";
        
    private static final String GET = "" +
            "SELECT " + _DATA + " FROM " + TABLE_NAME + " WHERE " + _ID + " = ?";
    
    FileUploadSQL(ValidConnection connection){
        super(connection);
    }

    void createTable(){
        super.createTable(CREATE_TABLE);
    }

    String add(byte[] data){
        UUID fieldId = UUID.randomUUID();

        try(PreparedStatement pstmt = connection.get().prepareStatement(ADD)){
            pstmt.setString(1, fieldId.toString());
            pstmt.setBytes(2, data);
            pstmt.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }

        return fieldId.toString();
    }
    
    byte[] get(String fileId){
        try (PreparedStatement pstmt = connection.get().prepareStatement(GET)){
            pstmt.setString(1, fileId);
            pstmt.execute();

            if(pstmt.getResultSet() != null && pstmt.getResultSet().next()){
                return pstmt.getResultSet().getBytes("data");
            }
        } catch (SQLException e){
            e.printStackTrace();
        }

        return new byte[]{};
    }

}
