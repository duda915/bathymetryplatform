import "primeflex/primeflex.css";
import "primeicons/primeicons.css";
import "primereact/resources/primereact.min.css";
import React from "react";
import EntryPoint from "./components/EntryPoint";
import LoadingSpinner from "./components/utility/loading/Spinner";
import Message from "./components/utility/messaging/Message";
import "./theme/PrimeReactTheme.scss";
import "./theme/Theme.css";
import "./theme/Utility.css";

export default function App() {
  return (
    <div>
      <LoadingSpinner />
      <Message />
      <EntryPoint />
    </div>
  );
}
