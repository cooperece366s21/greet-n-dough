import React from 'react';
import './App.css';
import Home from './pages/Home';
import About from './pages/About';
import Feed from './pages/Feed';
import Login from './pages/Login';
import Register from "./pages/Register";

import {
  BrowserRouter as Router,
  Switch,
  Route,
  Link
} from "react-router-dom";

function App() {
  return (
      <Router>

        <Switch>


            <Route exact path="/login" component={Login} />

            <Route exact path="/register" component={Register}/>

            <Route exact path="/about" component={About} />

            <Route exact path="/feed" component={Feed} />

            <Route exact path="/" component={Home} />

        </Switch>

      </Router>

  )
}

export default App;
