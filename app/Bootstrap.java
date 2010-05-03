import play.*;
import play.jobs.*;
import play.test.*;
import utils.RobotMessager;
 
import models.*;
 
@OnApplicationStart
public class Bootstrap extends Job {
 
    public void doJob() {
        // Check if the database is empty
        if(User.count() == 0) {
            Fixtures.load("initial-data.yml");
        }
        RobotMessager.init("zhu.shou@hotmail.com", "4l9c6m1");
        RobotMessager.login();
    }
 
}
