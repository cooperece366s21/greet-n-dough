package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

public class Post implements Serializable {

    private static final long serialVersionUID = 1L;
    private final int ID;
    private int userID;
    private List<Integer> iidList;
    private String title;
    private String contents;
    private final LocalDateTime timeCreated;

    public Post( String title, String contents, int pid, int uid ) {
        this( title, contents, pid, uid, new LinkedList<>() );
    }

    public Post( String title, String contents, int pid, int uid, List<Integer> iidList ) {
        this( title, contents, pid, uid, iidList, LocalDateTime.now() );
    }

    public Post( String title, String contents, int pid, int uid, List<Integer> iidList, LocalDateTime timeCreated ) {

        this.ID = pid;
        this.userID = uid;
        this.iidList = iidList;
        this.title = title;
        this.contents = contents;
        this.timeCreated = timeCreated;

    }

    public int getID() {
        return this.ID;
    }

    public int getUserID() {
        return this.userID;
    }

    public List<Integer> getImageIDList() {
        return this.iidList;
    }

    public String getTitle() {
        return this.title;
    }

    public String getContents() {
        return this.contents;
    }

    public LocalDateTime getTime() {
        return this.timeCreated;
    }

    public void setContents( String contents ) {
        this.contents = contents;
    }

    public boolean equals( Post tempPost ) {

        return  this.getID() == tempPost.getID() &&
                this.getUserID() == tempPost.getUserID() &&
                this.getTitle().equals( tempPost.getTitle() ) &&
                this.getContents().equals( tempPost.getContents() ) &&
                this.getTime().equals( tempPost.getTime() ) &&
                this.getImageIDList().equals( tempPost.getImageIDList() );

    }

}
