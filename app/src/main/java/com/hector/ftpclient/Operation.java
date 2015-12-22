package com.hector.ftpclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPFile;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import it.sauronsoftware.ftp4j.FTPListParseException;

/**
 * Created by Hector on 15/12/21.
 */
public class Operation {

    public boolean login(FTPClient client, String ip, int port, String username, String password) {
        try {
            client.connect(ip, port);
            client.login(username, password);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (FTPIllegalReplyException e) {
            e.printStackTrace();
            return false;
        } catch (FTPException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public List<FTPFile> getFiles(FTPClient client) {
        List<FTPFile> files = new ArrayList<>();
        try {
            client.currentDirectory();
            files = Arrays.asList(client.list());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (FTPIllegalReplyException e) {
            e.printStackTrace();
            return null;
        } catch (FTPException e) {
            e.printStackTrace();
            return null;
        } catch (FTPDataTransferException e) {
            e.printStackTrace();
            return null;
        } catch (FTPListParseException e) {
            e.printStackTrace();
            return null;
        } catch (FTPAbortedException e) {
            e.printStackTrace();
            return null;
        }
        return files;
    }

    public List<FTPFile> getFiles(FTPClient client, String dirName) {
        List<FTPFile> files = new ArrayList<>();
        try {
            client.changeDirectory(dirName);
            client.currentDirectory();
            files = Arrays.asList(client.list());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (FTPIllegalReplyException e) {
            e.printStackTrace();
            return null;
        } catch (FTPException e) {
            e.printStackTrace();
            return null;
        } catch (FTPDataTransferException e) {
            e.printStackTrace();
            return null;
        } catch (FTPListParseException e) {
            e.printStackTrace();
            return null;
        } catch (FTPAbortedException e) {
            e.printStackTrace();
            return null;
        }
        return files;
    }

    public List<FTPFile> getParentFiles(FTPClient client) {
        List<FTPFile> files = new ArrayList<>();
        try {
            client.changeDirectoryUp();
            client.currentDirectory();
            files = Arrays.asList(client.list());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (FTPIllegalReplyException e) {
            e.printStackTrace();
            return null;
        } catch (FTPException e) {
            e.printStackTrace();
            return null;
        } catch (FTPDataTransferException e) {
            e.printStackTrace();
            return null;
        } catch (FTPListParseException e) {
            e.printStackTrace();
            return null;
        } catch (FTPAbortedException e) {
            e.printStackTrace();
            return null;
        }
        return files;
    }

    public boolean newDir(FTPClient client, String dirName) {
        try {
            client.createDirectory(dirName);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (FTPIllegalReplyException e) {
            e.printStackTrace();
            return false;
        } catch (FTPException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean updateName(FTPClient client, String oldName, String newName) {
        try {
            client.rename(oldName, newName);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (FTPIllegalReplyException e) {
            e.printStackTrace();
            return false;
        } catch (FTPException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean deleteFile(FTPClient client, String fileName) {
        try {
            client.deleteFile(fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (FTPIllegalReplyException e) {
            e.printStackTrace();
            return false;
        } catch (FTPException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


}
