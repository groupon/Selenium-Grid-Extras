package selenium_grid_extras_jenkins.selenium_grid_extras_jenkins;


import hudson.Extension;
import hudson.Plugin;
import hudson.model.Action;
import hudson.model.RootAction;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

@Extension

public class MainRootAction extends Plugin implements Action, RootAction {

    public static final String GRID_EXTRAS_ENDPOINT = "/gridExtras";
    public static final String HUB_URL = "http://localhost";

    @Override
    public String getIconFileName() {
        return "up.png";
    }

    @Override
    public String getDisplayName() {
        return "Selenium Grid Extras";
    }

    @Override
    public String getUrlName() {
        return GRID_EXTRAS_ENDPOINT;
    }

    @Override
    public void start(){
        System.out.println("Start");
    }

    public String doNodes(StaplerRequest req, StaplerResponse rsp) {
        rsp.setContentType("application/json");
        return "{}";
    }

    public String doHubConsole(StaplerRequest req, StaplerResponse rsp){
        rsp.setContentType("text/html");
        return "<html><body><h1>hi</h1></body></html>";
    }
}
