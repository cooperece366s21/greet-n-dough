import * as React from 'react';
import './App.css';
import Home from './pages/Home';
import About from './pages/About';
import Feed from './pages/Feed';
import Login from './pages/Login';
import Register from "./pages/Register";
import { ChakraProvider } from "@chakra-ui/react";

import {
  BrowserRouter as Router,
  Switch,
  Route,
  Link
} from "react-router-dom";

function App() {
  return (
      <ChakraProvider>

          <Router>

            <Switch>

                <Route exact path="/login" component={Login} />

                <Route exact path="/register" component={Register}/>

                <Route exact path="/about" component={About} />

                <Route exact path="/feed" component={Feed} />

                <Route exact path="/" component={Home} />

            </Switch>

          </Router>

      </ChakraProvider>
  )
}

export default App;
