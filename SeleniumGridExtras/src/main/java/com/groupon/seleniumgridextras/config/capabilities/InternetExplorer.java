package com.groupon.seleniumgridextras.config.capabilities;

public class InternetExplorer extends Capability {

  @Override
  public String getWebDriverClass() {
    return "org.openqa.selenium.ie.InternetExplorerDriver";
  }

  public InternetExplorer() {
    this.put("maxInstances", 1);
    this.put("seleniumProtocol", "WebDriver");
    setBrowser(getWDStyleName());
  }

  @Override
  public String getIcon() {
    return "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAMAAAAoLQ9TAAACT1BMVEUAAABjrdsztv/6/PMAQYgAUaQFhv/1rD4yV1cAofh31v+hgjoCp//ztRD90jkaqPH79scATK0AnuTw04pbjK3i6vQBdLURsP89lPGff0cscp8Utv9PXXGpnXdeOwtEV3MAoO8AouxKW28ATpxJV2u/19UDOnEwuP8BPn0AdLm+w8tJY3Tw1Y7/tySmwcH/qTXp7vWT2MIvw/8dv/8vp/4AnPkOdv8Al/YATZV71P/9+50CSYcxaJ5As/5TsPz8wDbZpGHIrmb6/O8PYdzi5OlycERYiahoYUf22IE8wf8AWJtawv77+91n0v+hlCAATo9Xy/x0e4cAIlwoneU9m/K/qibN0NHe4Hz911UFr/8ArvZOyP/+wEVrz//AnBP7vBXU3HDDxHOjjE37yn7p380Ls//7zDYAld40dbb7/NY4ca/4zkdbzf4Ak9pcy//m6/M1SEVMNiTe186eez0AoPwDj/YAmN7/100gVH5TodUXrf8rpuUAkuZzgmc/teX/1UNzjYb99JKVhVn7wyb69eXsp0X/tzL+3WYfX4yNiW5iclT83ra4xX3nrQvQ1d0AP4YAQo+Fp8D8+7v/3HXHups/YnQBbK3S2+P7zkQAUpv7/NNHaXopuvjGq14AkNdIcZu6zYGFgFD7/M7g4+fo6+ORnnSFo7mFh4NBhLKLnanv8veso1QXlvg/lc4AaKjpvEQqt//1xzgAeMMAluzZ4ekKnf/K33f69NkAofIAhc9ezv4AMGMDQIHBl0AAPYLxw0wpNC+PhDgzt/9w0v+jzeXuc8xTAAAAAXRSTlMAQObYZgAAAQBJREFUeF5FTsOORAEA69hc27Zt27Zt27Zt2zY+bPMyyWxPbdO0xT8eAj8uZHol/Leppf2+5GSrO9uUkHKk72ermQa/tg3ACwBI5PLNjriAzjB2obkakVdIyeSHVx3EuJWGFOsBOFtNyE9Xr8bRqW1kbBRQcTi6OPbJNFmZ87F3V1QFdvcSMypfHOxqWTetgjpdYDqr5yt6KVhEo+lMUt+1QXG+4ulHnO9I/4V2mQ1PGK0rjx9P0Q3uuBpYHrBwdXyqT9YsSwt6bH71xbZHjaXhtdjG6cc66dLFGCjqV0J8o7yWSl7B/OACAO9Uz9mRHM6QrP/+LQhAyOh7W8uVjvwBmBRMNfnvVUgAAAAASUVORK5CYII=";
  }

}
