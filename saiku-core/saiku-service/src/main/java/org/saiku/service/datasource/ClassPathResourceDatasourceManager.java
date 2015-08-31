/*  
 *   Copyright 2014 OSBI Ltd
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.saiku.service.datasource;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.VFS;
import org.saiku.database.dto.MondrianSchema;
import org.saiku.datasources.connection.RepositoryFile;
import org.saiku.datasources.datasource.SaikuDatasource;
import org.saiku.datasources.datasource.SaikuDatasource.Type;
import org.saiku.repository.AclEntry;
import org.saiku.repository.IRepositoryManager;
import org.saiku.repository.IRepositoryObject;
import org.saiku.repository.JackRabbitRepositoryManager;
import org.saiku.service.user.UserService;
import org.saiku.service.util.exception.SaikuServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ClassPathResourceDatasourceManager implements IDatasourceManager {
    private static final Logger log = LoggerFactory.getLogger(ClassPathResourceDatasourceManager.class);

    private IRepositoryManager irm;

    private UserService userService;

    private String configurationpath;
    private String datadir;
    private String repoPassword;
    private URL repoURL;

    private Map<String, SaikuDatasource> datasources =
            Collections.synchronizedMap( new HashMap<String, SaikuDatasource>() );

    public ClassPathResourceDatasourceManager() {
    }

    public ClassPathResourceDatasourceManager(String path) {
        try {
            setPath(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPath(String path) {
        FileSystemManager fileSystemManager;
        try {
            fileSystemManager = VFS.getManager();

            FileObject fileObject;
            fileObject = fileSystemManager.resolveFile( path );
            if ( fileObject == null ) {
                throw new IOException( "File cannot be resolved: " + path );
            }
            if ( !fileObject.exists() ) {
                throw new IOException( "File does not exist: " + path );
            }
            repoURL = fileObject.getURL();
            if ( repoURL == null ) {
                throw new Exception( "Cannot load connection repository from path: " + path );
            } else {
                load();
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }

    }

    public void load() {
        irm = JackRabbitRepositoryManager.getJackRabbitRepositoryManager(configurationpath, datadir, repoPassword, null);
        try {
            irm.start(userService);
        } catch (RepositoryException e) {
            log.error("Could not start repo", e);
        }

        datasources.clear();
        try {
            if ( repoURL != null ) {
                File[] files = new File( repoURL.getFile() ).listFiles();

                for ( File file : files ) {
                    if ( !file.isHidden() ) {
                        Properties props = new Properties();
                        props.load( new FileInputStream( file ) );
                        String name = props.getProperty( "name" );
                        String type = props.getProperty( "type" );
                        if ( name != null && type != null ) {
                            Type t = SaikuDatasource.Type.valueOf( type.toUpperCase() );
                            SaikuDatasource ds = new SaikuDatasource( name, t, props );
                            datasources.put( name, ds );
                        }
                    }
                }
            } else {
                throw new Exception( "repo URL is null" );
            }
        } catch ( Exception e ) {
            throw new SaikuServiceException( e.getMessage(), e );
        }
    }

    public void unload() {
        irm.shutdown();
    }

    public SaikuDatasource addDatasource( SaikuDatasource datasource ) {
        try {
            String uri = repoURL.toURI().toString();
            if ( uri != null && datasource != null ) {
                uri += datasource.getName().replace( " ", "_" );
                File dsFile = new File( new URI( uri ) );
                if ( dsFile.exists() ) {
                    dsFile.delete();
                } else {
                    dsFile.createNewFile();
                }
                FileWriter fw = new FileWriter( dsFile );
                Properties props = datasource.getProperties();
                props.store( fw, null );
                fw.close();
                datasources.put( datasource.getName(), datasource );
                return datasource;

            } else {
                throw new SaikuServiceException( "Cannot save datasource because uri or datasource is null uri("
                        + ( uri == null ) + ")" );
            }
        } catch ( Exception e ) {
            throw new SaikuServiceException( "Error saving datasource", e );
        }
    }

    public SaikuDatasource setDatasource( SaikuDatasource datasource ) {
        return addDatasource( datasource );
    }

    public List<SaikuDatasource> addDatasources( List<SaikuDatasource> datasources ) {
        for ( SaikuDatasource ds : datasources ) {
            addDatasource( ds );
        }
        return datasources;
    }

    public boolean removeDatasource( String datasourceName ) {
        try {
            String uri = repoURL.toURI().toString();
            if ( uri != null ) {
                // seems like we don't have to do this anymore
                //uri.toString().endsWith(String.valueOf(File.separatorChar))) {
                uri += datasourceName;
                File dsFile = new File( new URI( uri ) );
                if ( dsFile.delete() ) {
                    datasources.remove( datasourceName );
                    return true;
                }
            }
            throw new Exception( "Cannot delete datasource file uri:" + uri );
        } catch ( Exception e ) {
            throw new SaikuServiceException( "Cannot delete datasource", e );
        }
    }

    public boolean removeSchema(String schemaName) {
        return false;
    }

    public Map<String, SaikuDatasource> getDatasources() {
        return datasources;
    }

    public SaikuDatasource getDatasource( String datasourceName ) {
        return datasources.get( datasourceName );
    }

    public void addSchema(String file, String path, String name) {

    }

    public List<MondrianSchema> getMondrianSchema() {
        return null;
    }

    public MondrianSchema getMondrianSchema(String catalog) {
        return null;
    }

    public RepositoryFile getFile(String file) {
        return null;
    }

    public String getFileData(String file, String username, List<String> roles) {
        try {
            return irm.getFile(file, username, roles);
        } catch (RepositoryException e) {
            log.error("Could not get file "+file, e);
        }
        return null;
    }

    public String getInternalFileData(String file) {
        return null;
    }

    public String saveFile(String path, Object content, String user, List<String> roles) {
        try {
            irm.saveFile(content, path, user, "nt:saikufiles", roles);
            return "Save Okay";
        } catch (RepositoryException e) {
            log.error("Save Failed", e);
            return "Save Failed: " + e.getLocalizedMessage();
        }
    }

    public String removeFile(String path, String user, List<String> roles) {
        try {
            irm.removeFile(path, user, roles);
            return "Remove Okay";
        } catch (RepositoryException e) {
            log.error("Save Failed", e);
            return "Save Failed: " + e.getLocalizedMessage();
        }
    }

    public String moveFile(String source, String target, String user, List<String> roles) {
        try {
            irm.moveFile(source, target, user, roles);
            return "Move Okay";
        } catch (RepositoryException e) {
            log.error("Move Failed", e);
            return "Move Failed: " + e.getLocalizedMessage();
        }
    }

    public String saveInternalFile(String path, Object content, String type) {
        try {
            irm.saveInternalFile(content, path, type);
            return "Save Okay";
        } catch (RepositoryException e) {
            e.printStackTrace();
            return "Save Failed: " + e.getLocalizedMessage();
        }
    }

    public String saveBinaryInternalFile(String path, InputStream content, String type) {
        try {
            irm.saveBinaryInternalFile(content, path, type);
            return "Save Okay";
        } catch (RepositoryException e) {
            e.printStackTrace();
            return "Save Failed: " + e.getLocalizedMessage();
        }
    }
    public void removeInternalFile(String filePath) {
        try{
            irm.removeInternalFile(filePath);
        } catch(RepositoryException e) {
            log.error("Remove file failed: " + filePath);
            e.printStackTrace();
        }
    }

    public List<IRepositoryObject> getFiles(String type, String username, List<String> roles) {
        try {
            return irm.getAllFiles(type, username, roles);
        } catch (RepositoryException e) {
            log.error("Get failed", e);
        }
        return null;
    }

    public List<IRepositoryObject> getFiles(String type, String username, List<String> roles, String path) {
        try {
            return irm.getAllFiles(type, username, roles, path);
        } catch (RepositoryException e) {
            log.error("Get failed", e);
        }
        return null;
    }

    public javax.jcr.Node getFiles() {
        return null;
    }

    public void createUser(String user) {

    }

    public void deleteFolder(String folder) {
        try {
            irm.deleteFolder(folder);
        } catch (RepositoryException e) {
            log.error("Delete User Failed", e);
        }
    }

    public AclEntry getACL(String object, String username, List<String> roles) {
        return irm.getACL(object, username, roles);
    }

    public void setACL(String object, String acl, String username, List<String> roles) {
        try {
            irm.setACL(object, acl, username, roles);
        } catch (RepositoryException e) {
            log.error("Set ACL Failed", e);
        }
    }

    public List<MondrianSchema> getInternalFilesOfFileType(String type){
        try {
            return irm.getInternalFilesOfFileType(type);
        } catch (RepositoryException e) {
            log.error("Get internal file failed", e);
        }
        return null;
    }

    public void createFileMixin(String type) throws RepositoryException {
        irm.createFileMixin(type);
    }

    public byte[] exportRepository(){
        try {
            return irm.exportRepository();

        } catch (RepositoryException e) {
            log.error("could not export repository", e);
        } catch (IOException e) {
            log.error("could not export repository IO issue", e);
        }
        return null;
    }

    public void restoreRepository(byte[] data) {
        try {
            irm.restoreRepository(data);
        }
        catch (Exception e){
            log.error("Could not restore export", e);
        }
    }

    public boolean hasHomeDirectory(String name) {
        try{
            Node eturn = irm.getHomeFolder(name);
            if (eturn!=null){
                return true;
            }
            return false;
        } catch (RepositoryException e) {
            log.error("could not get home directory");
        }
        return false;
    }

    public void restoreLegacyFiles(byte[] data) {

    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public String getFoodmartschema() {
        return null;
    }

    public void setFoodmartschema(String schema) {

    }

    public void setFoodmartdir(String dir) {

    }

    public String getFoodmartdir() {
        return null;
    }

    public String getDatadir() {
        return datadir;
    }

    public void setDatadir(String datadir) {
        this.datadir = datadir;
    }

    public void setFoodmarturl(String foodmarturl) {

    }

    public String getFoodmarturl() {
        return null;
    }

    public UserService getUserService() {
        return userService;
    }

    public String getRepoPassword() {
        return repoPassword;
    }

    public void setRepoPassword(String repoPassword) {
        this.repoPassword = repoPassword;
    }

    public String getConfigurationpath() {
        return configurationpath;
    }

    public void setConfigurationpath(String configurationpath) {
        this.configurationpath = configurationpath;
    }
}
