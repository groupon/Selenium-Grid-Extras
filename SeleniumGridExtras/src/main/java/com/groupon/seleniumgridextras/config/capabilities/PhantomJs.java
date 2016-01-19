package com.groupon.seleniumgridextras.config.capabilities;

public class PhantomJs extends Capability {

    @Override
    public String getWebDriverClass() {
        return "org.openqa.selenium.phantomjs.PhantomJSDriver";
    }

    @Override
    public String getIcon() {
        return "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsQAAA7EAZUrDhsAAAHVSURBVDhPbZM5SENBEIZT2FgIdjZ2FoKFYGFhIRZ2gmAjgoUgCBYiCIIg2AgWIhYiNoIgYqWdvYhYCvEOuQ05zJ2YxMRcLxnnn7Avm2cGhp095tuZ2VkbdZEmq71Upu1IkhY+I7QSjNJlJk8Fo9E6oMk/QLXZpNVgjGx2Jw28emjSHaQJZ4BGHH4a5xFgXToA4M/zjT3sPMoOcJ72tBXzoXcfOcqVlgNLB+A6WxBn3AgARjjpMKxNsV3jSCEmwOAFFe7gm1dS6H1yUd+zS+z+F7c4AoIo7n9K4mcCAtWa5IlDiAJOVgUcgDEeN8MJ8TMBt4UiDX/45YAeAW6GrUcA0JwvLDUzATe5ghkBVOWt23DGXNl1TtsEoIA4qAA4YFW1p2pVbmiA40SmA9BNVURQpJusGy0AHmQ5EBWqfki3rWt4iQd+CQEUGw3pMmwAokBYs0Ixx4ho19CxAICEkOAwy9VdD8UlohlviJZ43PlK0hGnuMhdqhcULyEAbOBttyIJqcVZ6ptOWQ/iaXN+ns4JBP9kg3tgP5amPVYB+CpVyhqGvCta9IIPwxk3nySzdJXN02E8I/ZuNEWPpV9JGx9PAFYp8SbSCnN3qg+MiuNL33HDtYXoD0aZUVayQnR5AAAAAElFTkSuQmCC";
    }
}