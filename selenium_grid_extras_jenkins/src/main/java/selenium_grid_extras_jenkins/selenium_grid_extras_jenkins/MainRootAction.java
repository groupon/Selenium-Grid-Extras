package selenium_grid_extras_jenkins.selenium_grid_extras_jenkins;


import hudson.Extension;
import hudson.Plugin;
import hudson.model.Action;
import hudson.model.RootAction;

@Extension

public class MainRootAction extends Plugin implements Action, RootAction {

    public static final String GRID_EXTRAS_ENDPOINT = "/gridExtras";

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
}
