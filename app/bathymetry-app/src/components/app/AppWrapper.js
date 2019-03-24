import React from "react";
import { HashRouter as Router, Route } from "react-router-dom";
import "./AppWrapper.css";
import DataManager from "./datamanager/DataManager";
import DataSelector from "./dataselector/DataSelector";
import Map from "./map/Map";
import MenuPanel from "./menu/MenuPanel";
import Settings from "./usersettings/Settings";

export default function AppWrapper() {
  return (
    <div>
      <div className="p-grid p-nogutter">
        <div className="p-col-12 p-lg-3">
          <MenuPanel />
        </div>
        <Router>
          <div className="p-col-12 p-lg-9">
            <Route exact path="/" component={Map} />
            <Route path="/select" component={DataSelector} />
            <Route path="/mydata" component={DataManager} />
            <Route path="/settings" component={Settings} />
          </div>
        </Router>
      </div>
    </div>
  );
}
