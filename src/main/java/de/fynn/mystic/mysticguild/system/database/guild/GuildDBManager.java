package de.fynn.mystic.mysticguild.system.database.guild;

import de.fynn.mystic.mysticguild.community.Status;
import de.fynn.mystic.mysticguild.community.alianz.Alianz;
import de.fynn.mystic.mysticguild.community.alianz.AlianzManager;
import de.fynn.mystic.mysticguild.community.alianz.OfflineAlianz;
import de.fynn.mystic.mysticguild.community.guild.OfflineGuild;
import de.fynn.mystic.mysticguild.community.permission.PermissionDataFactory;
import de.fynn.mystic.mysticguild.community.role.Role;
import de.fynn.mystic.mysticguild.system.database.CommunityDBManager;
import de.fynn.mystic.mysticguild.system.database.DBConnector;
import de.fynn.mystic.mysticguild.system.file.ConfigHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GuildDBManager extends CommunityDBManager {

    private String table;
    private String schema;
    private OfflineGuild parent;
    private final static DBConnector CONNECTOR = DBConnector.getInstance();


    public GuildDBManager(OfflineGuild parent) {
        super(parent);
        this.parent = parent;
        schema = ConfigHandler.getDBSchema();
        table = schema+"."+parent.getUuid();
    }

    @Override
    public void addMember(UUID uuid, String displayname) {
        CONNECTOR.executeSQLAsync("INSERT INTO "+table+" (uuid,displayname,role) VALUES ('"+uuid.toString()+"','"+displayname+"','default');");
    }

    @Override
    public void removeMember(UUID uuid) {
        CONNECTOR.executeSQLAsync("DELETE FROM "+table+" WHERE uuid = '"+uuid.toString()+"';");
    }

    @Override
    public void rename(String name) {
        CONNECTOR.updateAsync("UPDATE "+schema+".guild SET name = '"+name+"' WHERE uuid = '"+parent.getUuid().toString()+"';");
    }

    @Override
    public void setPrefix(String prefix) {
        CONNECTOR.updateAsync("UPDATE "+schema+".guild SET prefix = '"+prefix+"' WHERE uuid = '"+parent.getUuid().toString()+"';");
    }

    @Override
    public void setLevel(int level) {
        CONNECTOR.updateAsync("UPDATE "+schema+".guild SET level = "+level+" WHERE uuid = '"+parent.getUuid().toString()+"';");
    }

    @Override
    public void setXP(double xp) {
        CONNECTOR.updateAsync("UPDATE "+schema+".guild SET xp = "+xp+" WHERE uuid = '"+parent.getUuid().toString()+"';");
    }

    @Override
    public void setStatus(Status status) {
        CONNECTOR.updateAsync("UPDATE "+schema+".guild SET status = "+status.value+" WHERE uuid = '"+parent.getUuid().toString()+"';");
    }

    public void setInventoryUpgrades(int inventoryUpgrades){
        CONNECTOR.executeSQLAsync("UPDATE "+schema+".guild SET invUpgrades = "+inventoryUpgrades+";");
    }

    @Override
    public String getName() {
        ResultSet result = CONNECTOR.executeQuerry("SELECT name FROM "+schema+".guild WHERE uuid = '"+parent.getUuid()+"';");
        try {
            result.next();
            return result.getString(1);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return "";
        }
    }

    @Override
    public String getDisplayname(UUID uuid) {
        ResultSet result = CONNECTOR.executeQuerry("SELECT displayname FROM "+table+" WHERE uuid = '"+uuid+"';");
        try {
            result.next();
            return result.getString(1);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return "";
        }
    }

    @Override
    public String getPrefix() {
        ResultSet result = CONNECTOR.executeQuerry("SELECT prefix FROM "+schema+".guild WHERE uuid = '"+parent.getUuid()+"';");
        try {
            result.next();
            return result.getString(1);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return "";
        }
    }

    @Override
    public UUID getOwner() {
        ResultSet result = CONNECTOR.executeQuerry("SELECT owner FROM "+schema+".guild WHERE uuid = '"+parent.getUuid()+"';");
        try {
            result.next();
            return UUID.fromString(result.getString(1));
        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public int getLevel() {
        ResultSet result = CONNECTOR.executeQuerry("SELECT level FROM "+schema+".guild WHERE uuid = '"+parent.getUuid()+"';");
        try {
            result.next();
            return result.getInt(1);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return -1;
        }
    }

    @Override
    public double getXP() {
        ResultSet result = CONNECTOR.executeQuerry("SELECT xp FROM "+schema+".guild WHERE uuid = '"+parent.getUuid()+"';");
        try {
            result.next();
            return result.getDouble(1);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return -1;
        }
    }

    @Override
    public List<UUID> getMembers() {
        List<UUID> members = new ArrayList<>();
        ResultSet result = CONNECTOR.executeQuerry("SELECT uuid FROM "+table+";");
        try {
            while (result.next()){
                members.add(UUID.fromString(result.getString(1)));
            }
        }catch (SQLException exception){
            exception.printStackTrace();
        }
        return members;
    }

    @Override
    public Status getStatus() {
        ResultSet result = CONNECTOR.executeQuerry("SELECT status FROM "+schema+".guild WHERE uuid = '"+parent.getUuid()+"';");
        try {
            result.next();
            return Status.values()[result.getInt(1)];
        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public int getInventoryUpgrades(){
        ResultSet result = CONNECTOR.executeQuerry("SELECT invUpgrades FROM "+schema+".guild WHERE uuid = '"+parent.getUuid()+"';");
        try {
            result.next();
            return result.getInt(1);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return -1;
        }
    }

    @Override
    public void addInvite(UUID uuid, String displayname) {
        CONNECTOR.executeSQLAsync("INSERT INTO "+table+".invited (uuid,displayname) VALUES ('"+uuid.toString()+"','"+displayname+"');");
    }

    @Override
    public boolean isInvited(UUID uuid) {
        ResultSet result = CONNECTOR.executeQuerry("SELECT uuid FROM "+parent+".invited WHERE uuid = '"+uuid+"';");
        try {
            return result.next();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    @Override
    public void addBan(UUID uuid, String displayname) {
        CONNECTOR.executeSQLAsync("INSERT INTO "+table+".banned (uuid,displayname) VALUES ('"+uuid.toString()+"','"+displayname+"');");
    }

    @Override
    public void addJoinRequest(UUID uuid, String displayname) {
        CONNECTOR.executeSQLAsync("INSERT INTO "+table+".joinRequests (uuid,displayname) VALUES ('"+uuid.toString()+"','"+displayname+"');");
    }

    @Override
    public void removeInvite(UUID uuid) {
        CONNECTOR.executeSQLAsync("DELETE FROM "+table+".invited WHERE uuid = '"+uuid.toString()+"';");
    }

    @Override
    public void removeBan(UUID uuid) {
        CONNECTOR.executeSQLAsync("DELETE FROM "+table+".banned WHERE uuid = '"+uuid.toString()+"';");
    }

    @Override
    public boolean isBanned(UUID uuid) {
        ResultSet result = CONNECTOR.executeQuerry("SELECT uuid FROM "+parent+".banned WHERE uuid = '"+uuid+"';");
        try {
            return result.next();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    @Override
    public void removeJoinRequest(UUID uuid) {
        CONNECTOR.executeSQLAsync("DELETE FROM "+table+".joinRequests WHERE uuid = '"+uuid.toString()+"';");
    }

    @Override
    public Role getDefaultRole() {
        ResultSet result = CONNECTOR.executeQuerry("SELECT defaultRole FROM "+schema+".guild WHERE uuid = '"+parent.getUuid().toString()+"';");
        try {
            result.next();
            String role = result.getString(1);
            result = CONNECTOR.executeQuerry("SELECT * FROM "+table+" WHERE role = '"+role+"';");
            result.next();
            return new Role(role,Integer.parseInt(result.getString(3)), new PermissionDataFactory().getGuildPermissionData(result.getString(2)));
        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public void delete() {
        CONNECTOR.executeSQLAsync("DELETE FROM "+schema+".guild WHERE uuid = '"+parent.getUuid().toString()+"';");
        CONNECTOR.executeSQLAsync("DROP TABLE "+table+";");
        CONNECTOR.executeSQLAsync("DROP TABLE "+table+".role;");
        CONNECTOR.executeSQLAsync("DROP TABLE "+table+".banned;");
        CONNECTOR.executeSQLAsync("DROP TABLE "+table+".invited;");
        CONNECTOR.executeSQLAsync("DROP TABLE "+table+".joinRequests;");
        CONNECTOR.executeSQLAsync("DELETE FROM "+schema+".war WHERE uuid_1 = '"+parent.getUuid().toString()+"' OR uuid_2 = '"+parent.getUuid().toString()+"';");
        OfflineAlianz alianz;
        if( (alianz = AlianzManager.getInstance().getGuildAlianz(parent.getUuid()))!=null){
            CONNECTOR.executeSQLAsync("DELETE FROM "+schema+"."+alianz.getUuid().toString()+" WHERE uuid = '"+parent.getUuid().toString()+"';");
        }
    }

}