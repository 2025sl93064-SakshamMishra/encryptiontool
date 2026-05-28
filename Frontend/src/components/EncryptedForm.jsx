import { useState } from "react";
import API from "../api/axios";
import Loader from "./Loader";

const ALGORITHMS = [
  { label: "AES-128-CBC", value: "AES_128" },
  { label: "AES-256-CBC", value: "AES_256" },
  { label: "Triple-DES-CBC", value: "TRIPLE_DES" },
  { label: "RSA-2048", value: "RSA" },
];

function EncryptedForm() {
  const [text, setText] = useState("");
  const [algorithm, setAlgorithm] = useState(ALGORITHMS[0].value);
  const [result, setResult] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleEncrypt = async (e) => {
    e.preventDefault();
    setError("");
    setResult("");
    setLoading(true);
    try {
      const response = await API.post("/api/encrypt/text", { text, algorithm });
      setResult(response.data.data?.result ?? JSON.stringify(response.data.data));
    } catch (err) {
      setError(err.response?.data?.message || "Encryption failed.");
    } finally {
      setLoading(false);
    }
  };

  const copyToClipboard = () => navigator.clipboard.writeText(result);

  return (
    <div className="crypto-form">
      {loading && <Loader />}
      <form onSubmit={handleEncrypt}>
        <div className="form-group">
          <label>Plain Text</label>
          <textarea
            rows={5}
            placeholder="Enter text to encrypt…"
            value={text}
            onChange={(e) => setText(e.target.value)}
            required
          />
        </div>
        <div className="form-group">
          <label>Algorithm</label>
          <select value={algorithm} onChange={(e) => setAlgorithm(e.target.value)}>
            {ALGORITHMS.map((a) => <option key={a.value} value={a.value}>{a.label}</option>)}
          </select>
        </div>
        {error && <div className="alert alert-error">{error}</div>}
        <button type="submit" className="btn btn-primary btn-full" disabled={loading}>
          {loading ? "Encrypting…" : "Encrypt"}
        </button>
      </form>
      {result && (
        <div className="result-box">
          <div className="result-header">
            <span>Encrypted Output</span>
            <button className="btn btn-ghost btn-sm" onClick={copyToClipboard}>Copy</button>
          </div>
          <pre className="result-text">{result}</pre>
        </div>
      )}
    </div>
  );
}

export default EncryptedForm;
