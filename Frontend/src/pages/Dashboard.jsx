import { useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import API from "../api/axios";
import EncryptedForm from "../components/EncryptedForm";
import DecryptedForm from "../components/DecryptedForm";
import Loader from "../components/Loader";

const ALGORITHMS = [
  { label: "AES-128-CBC", value: "AES_128" },
  { label: "AES-256-CBC", value: "AES_256" },
  { label: "Triple-DES-CBC", value: "TRIPLE_DES" },
  { label: "RSA-2048", value: "RSA" },
];

// ── Email Tab ────────────────────────────────────────────────────────────────
function EmailTab() {
  const [sendForm, setSendForm] = useState({ toEmail: "", subject: "", body: "", algorithm: ALGORITHMS[0].value });
  const [decryptForm, setDecryptForm] = useState({ encryptedText: "", algorithm: ALGORITHMS[0].value });
  const [sendResult, setSendResult] = useState("");
  const [decryptResult, setDecryptResult] = useState("");
  const [sendError, setSendError] = useState("");
  const [decryptError, setDecryptError] = useState("");
  const [sendLoading, setSendLoading] = useState(false);
  const [decryptLoading, setDecryptLoading] = useState(false);

  const handleSend = async (e) => {
    e.preventDefault();
    setSendError(""); setSendResult("");
    setSendLoading(true);
    try {
      const res = await API.post("/api/email/send-encrypted", sendForm);
      setSendResult(res.data.message ?? "Email sent successfully!");
    } catch (err) {
      setSendError(err.response?.data?.message || "Failed to send email.");
    } finally {
      setSendLoading(false);
    }
  };

  const handleDecrypt = async (e) => {
    e.preventDefault();
    setDecryptError(""); setDecryptResult("");
    setDecryptLoading(true);
    try {
      const res = await API.post("/api/email/decrypt", decryptForm);
      setDecryptResult(res.data.data?.originalMessage ?? JSON.stringify(res.data.data));
    } catch (err) {
      setDecryptError(err.response?.data?.message || "Failed to decrypt email.");
    } finally {
      setDecryptLoading(false);
    }
  };

  return (
    <div className="tab-section two-col">
      <div className="panel">
        <h3 className="panel-title">📤 Send Encrypted Email</h3>
        {sendLoading && <Loader />}
        <form onSubmit={handleSend}>
          <div className="form-group">
            <label>Recipient Email</label>
            <input type="email" placeholder="recipient@example.com" value={sendForm.toEmail}
              onChange={(e) => setSendForm({ ...sendForm, toEmail: e.target.value })} required />
          </div>
          <div className="form-group">
            <label>Subject</label>
            <input type="text" placeholder="Email subject" value={sendForm.subject}
              onChange={(e) => setSendForm({ ...sendForm, subject: e.target.value })} required />
          </div>
          <div className="form-group">
            <label>Message</label>
            <textarea rows={4} placeholder="Type your message…" value={sendForm.body}
              onChange={(e) => setSendForm({ ...sendForm, body: e.target.value })} required />
          </div>
          <div className="form-group">
            <label>Algorithm</label>
            <select value={sendForm.algorithm} onChange={(e) => setSendForm({ ...sendForm, algorithm: e.target.value })}>
              {ALGORITHMS.map((a) => <option key={a.value} value={a.value}>{a.label}</option>)}
            </select>
          </div>
          {sendError && <div className="alert alert-error">{sendError}</div>}
          {sendResult && <div className="alert alert-success">{sendResult}</div>}
          <button type="submit" className="btn btn-primary btn-full" disabled={sendLoading}>
            {sendLoading ? "Sending…" : "Send Encrypted Email"}
          </button>
        </form>
      </div>

      <div className="panel">
        <h3 className="panel-title">📥 Decrypt Received Email</h3>
        {decryptLoading && <Loader />}
        <form onSubmit={handleDecrypt}>
          <div className="form-group">
            <label>Encrypted Message</label>
            <textarea rows={4} placeholder="Paste the encrypted message…" value={decryptForm.encryptedText}
              onChange={(e) => setDecryptForm({ ...decryptForm, encryptedText: e.target.value })} required />
          </div>
          <div className="form-group">
            <label>Algorithm</label>
            <select value={decryptForm.algorithm} onChange={(e) => setDecryptForm({ ...decryptForm, algorithm: e.target.value })}>
              {ALGORITHMS.map((a) => <option key={a.value} value={a.value}>{a.label}</option>)}
            </select>
          </div>
          {decryptError && <div className="alert alert-error">{decryptError}</div>}
          {decryptResult && (
            <div className="result-box">
              <div className="result-header"><span>Decrypted Message</span></div>
              <pre className="result-text">{decryptResult}</pre>
            </div>
          )}
          <button type="submit" className="btn btn-primary btn-full" disabled={decryptLoading}>
            {decryptLoading ? "Decrypting…" : "Decrypt Email"}
          </button>
        </form>
      </div>
    </div>
  );
}

// ── Files Tab ────────────────────────────────────────────────────────────────
function FilesTab() {
  const [uploadFile, setUploadFile] = useState(null);
  const [exportLoading, setExportLoading] = useState(false);
  const [importLoading, setImportLoading] = useState(false);
  const [exportError, setExportError] = useState("");
  const [importError, setImportError] = useState("");
  const [importSuccess, setImportSuccess] = useState("");

  const handleExport = async () => {
    setExportError("");
    setExportLoading(true);
    try {
      const res = await API.get("/api/export/encrypt", { responseType: "blob" });
      const url = window.URL.createObjectURL(new Blob([res.data]));
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", "encrypted_data.enc");
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
    } catch (err) {
      setExportError(err.response?.data?.message || "Export failed.");
    } finally {
      setExportLoading(false);
    }
  };

  const handleImport = async (e) => {
    e.preventDefault();
    if (!uploadFile) return;
    setImportError(""); setImportSuccess("");
    setImportLoading(true);
    try {
      const formData = new FormData();
      formData.append("file", uploadFile);
      const res = await API.post("/api/export/decrypt", formData, {
        responseType: "blob",
        headers: { "Content-Type": "multipart/form-data" },
      });
      const url = window.URL.createObjectURL(new Blob([res.data]));
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", "decrypted_data.xlsx");
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
      setImportSuccess("File decrypted and downloaded as .xlsx!");
    } catch (err) {
      setImportError(err.response?.data?.message || "Import failed.");
    } finally {
      setImportLoading(false);
    }
  };

  return (
    <div className="tab-section two-col">
      <div className="panel">
        <h3 className="panel-title">⬇️ Export Encrypted Data</h3>
        <p className="panel-desc">Download all your encrypted records as a <code>.enc</code> file.</p>
        {exportError && <div className="alert alert-error">{exportError}</div>}
        <button className="btn btn-primary btn-full" onClick={handleExport} disabled={exportLoading}>
          {exportLoading ? "Exporting…" : "Download .enc File"}
        </button>
      </div>

      <div className="panel">
        <h3 className="panel-title">⬆️ Import & Decrypt File</h3>
        <p className="panel-desc">Upload a <code>.enc</code> file to decrypt and download as <code>.xlsx</code>.</p>
        <form onSubmit={handleImport}>
          <div className="form-group">
            <label>Select .enc File</label>
            <input type="file" accept=".enc" onChange={(e) => setUploadFile(e.target.files[0])} required />
          </div>
          {importError && <div className="alert alert-error">{importError}</div>}
          {importSuccess && <div className="alert alert-success">{importSuccess}</div>}
          <button type="submit" className="btn btn-primary btn-full" disabled={importLoading}>
            {importLoading ? "Processing…" : "Decrypt & Download .xlsx"}
          </button>
        </form>
      </div>
    </div>
  );
}

// ── Reports Tab ──────────────────────────────────────────────────────────────
function ReportsTab() {
  const [summary, setSummary] = useState(null);
  const [history, setHistory] = useState(null);
  const [files, setFiles] = useState(null);
  const [loading, setLoading] = useState("");
  const [error, setError] = useState("");

  const fetchData = async (type) => {
    setError(""); setLoading(type);
    const endpoints = {
      summary: "/api/reports/summary",
      history: "/api/reports/history",
      files: "/api/reports/files",
    };
    try {
      const res = await API.get(endpoints[type]);
      if (type === "summary") setSummary(res.data.data);
      if (type === "history") setHistory(Array.isArray(res.data.data) ? res.data.data : [res.data.data]);
      if (type === "files") setFiles(Array.isArray(res.data.data) ? res.data.data : [res.data.data]);
    } catch (err) {
      setError(err.response?.data?.message || `Failed to fetch ${type}.`);
    } finally {
      setLoading("");
    }
  };

  return (
    <div className="tab-section">
      <div className="reports-actions">
        <button className="btn btn-primary" onClick={() => fetchData("summary")} disabled={loading === "summary"}>
          {loading === "summary" ? "Loading…" : "Load Summary"}
        </button>
        <button className="btn btn-primary" onClick={() => fetchData("history")} disabled={loading === "history"}>
          {loading === "history" ? "Loading…" : "Load History"}
        </button>
        <button className="btn btn-primary" onClick={() => fetchData("files")} disabled={loading === "files"}>
          {loading === "files" ? "Loading…" : "Load File Records"}
        </button>
      </div>

      {error && <div className="alert alert-error">{error}</div>}

      {summary && (
        <div className="report-panel">
          <h4>Summary</h4>
          <div className="stats-grid">
            {Object.entries(summary).map(([key, val]) => (
              <div key={key} className="stat-card">
                <span className="stat-label">{key}</span>
                <span className="stat-value">{String(val)}</span>
              </div>
            ))}
          </div>
        </div>
      )}

      {history && (
        <div className="report-panel">
          <h4>Operation History</h4>
          <div className="table-wrap">
            <table className="data-table">
              <thead>
                <tr>{Object.keys(history[0] || {}).map((k) => <th key={k}>{k}</th>)}</tr>
              </thead>
              <tbody>
                {history.map((row, i) => (
                  <tr key={i}>{Object.values(row).map((v, j) => <td key={j}>{String(v)}</td>)}</tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {files && (
        <div className="report-panel">
          <h4>File Records</h4>
          <div className="table-wrap">
            <table className="data-table">
              <thead>
                <tr>{Object.keys(files[0] || {}).map((k) => <th key={k}>{k}</th>)}</tr>
              </thead>
              <tbody>
                {files.map((row, i) => (
                  <tr key={i}>{Object.values(row).map((v, j) => <td key={j}>{String(v)}</td>)}</tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
}

// ── Dashboard Home ────────────────────────────────────────────────────────────
const STAT_ICONS = ["○", "🔒", "🔒", "✉", "📊"];
const STAT_LABELS = ["Total Operations", "Encryptions", "Decryptions", "Emails Sent", "Exports"];
const STAT_KEYS = ["totalOperations", "encryptions", "decryptions", "emailsSent", "exports"];

const QUICK_ACTIONS = [
  {
    icon: "abc",
    title: "Encrypt Text",
    desc: "Encrypt or decrypt text with AES, 3DES, RSA",
    section: "text",
    color: "blue",
  },
  {
    icon: "✉",
    title: "Email Encryption",
    desc: "Send or decrypt an encrypted email",
    section: "email",
    color: "green",
  },
  {
    icon: "📊",
    title: "Excel Export",
    desc: "Export your data as encrypted .enc file",
    section: "export",
    color: "blue",
  },
  {
    icon: "📋",
    title: "Reports",
    desc: "View your full operation history",
    section: "reports",
    color: "yellow",
  },
];

function DashboardHome({ onNavigate }) {
  const [stats, setStats] = useState(null);

  useEffect(() => {
    API.get("/api/reports/summary")
      .then((res) => setStats(res.data.data))
      .catch(() => {});
  }, []);

  const statValues = STAT_KEYS.map((k) => (stats ? (stats[k] ?? 0) : 0));

  return (
    <div className="dash-home">
      <div className="dash-home-header">
        <h2>Dashboard</h2>
        <p className="dash-home-sub">Overview of your encryption activity</p>
      </div>

      <div className="dash-stats-row">
        {STAT_LABELS.map((label, i) => (
          <div key={label} className="dash-stat-card">
            <span className="dash-stat-icon">{STAT_ICONS[i]}</span>
            <span className="dash-stat-value">{statValues[i]}</span>
            <span className="dash-stat-label">{label}</span>
          </div>
        ))}
      </div>

      <div className="dash-quick-label">Quick Actions</div>
      <div className="dash-quick-grid">
        {QUICK_ACTIONS.map((a) => (
          <button
            key={a.section}
            className={`dash-quick-card dash-quick-${a.color}`}
            onClick={() => onNavigate(a.section)}
          >
            <span className="dash-quick-icon">{a.icon}</span>
            <div className="dash-quick-info">
              <span className="dash-quick-title">{a.title}</span>
              <span className="dash-quick-desc">{a.desc}</span>
            </div>
            <span className="dash-quick-arrow">→</span>
          </button>
        ))}
      </div>
    </div>
  );
}

// ── Dashboard Root ────────────────────────────────────────────────────────────
function Dashboard() {
  const navigate = useNavigate();
  const location = useLocation();
  const params = new URLSearchParams(location.search);
  const section = params.get("section");

  const goToSection = (s) => navigate(`/dashboard?section=${s}`);

  return (
    <div className="dashboard">
      {!section && <DashboardHome onNavigate={goToSection} />}

      {section === "text" && (
        <div className="tab-content">
          <div className="tab-section two-col">
            <div className="panel">
              <h3 className="panel-title">🔒 Encrypt Text</h3>
              <EncryptedForm />
            </div>
            <div className="panel">
              <h3 className="panel-title">🔓 Decrypt Text</h3>
              <DecryptedForm />
            </div>
          </div>
        </div>
      )}

      {section === "email" && (
        <div className="tab-content"><EmailTab /></div>
      )}

      {section === "export" && (
        <div className="tab-content"><FilesTab /></div>
      )}

      {section === "reports" && (
        <div className="tab-content"><ReportsTab /></div>
      )}
    </div>
  );
}

export default Dashboard;
