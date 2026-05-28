import { Link } from "react-router-dom";
import { useState, useEffect } from "react";

const ALGORITHMS = ["AES-128-CBC", "AES-256-CBC", "Triple-DES-CBC", "RSA-2048"];

const DEMOS = [
  {
    plain: "Hello, World!",
    algo: "AES-256-CBC",
    cipher: "U2FsdGVkX1+3mXv8KqP2nR7cT0wYhBzDfJsLiNpMoE=",
  },
  {
    plain: "Top Secret Data",
    algo: "RSA-2048",
    cipher: "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA...",
  },
  {
    plain: "Confidential: Q4 Report",
    algo: "AES-128-CBC",
    cipher: "a8F3bK2cL9dE5fG1hJ7kM4nP6qR0sT2uV8wX1yZ3=",
  },
];

const STATS = [
  { value: "4", label: "Encryption Algorithms" },
  { value: "256", label: "Max Key Bits" },
  { value: "100%", label: "Open Standard" },
  { value: "∞", label: "Operations" },
];

const STEPS = [
  {
    num: "01",
    title: "Create Account",
    desc: "Sign up in seconds. No credit card required.",
    icon: "👤",
  },
  {
    num: "02",
    title: "Choose Algorithm",
    desc: "Pick from AES, Triple-DES, or RSA-2048 based on your need.",
    icon: "⚙️",
  },
  {
    num: "03",
    title: "Encrypt & Share",
    desc: "Encrypt text, send secure emails, or export encrypted files.",
    icon: "🚀",
  },
];

function LiveDemo() {
  const [idx, setIdx] = useState(0);
  const [fading, setFading] = useState(false);

  useEffect(() => {
    const timer = setInterval(() => {
      setFading(true);
      setTimeout(() => {
        setIdx((i) => (i + 1) % DEMOS.length);
        setFading(false);
      }, 350);
    }, 3200);
    return () => clearInterval(timer);
  }, []);

  const demo = DEMOS[idx];

  return (
    <div className="demo-card">
      <div className="demo-card-header">
        <div className="demo-dots">
          <span className="dot dot-red" />
          <span className="dot dot-yellow" />
          <span className="dot dot-green" />
        </div>
        <span className="demo-label">Live Preview</span>
        <span className="demo-algo-tag">{demo.algo}</span>
      </div>
      <div className={`demo-body ${fading ? "fading" : ""}`}>
        <div className="demo-pane">
          <p className="demo-pane-label">Plain Text</p>
          <p className="demo-pane-value plain">{demo.plain}</p>
        </div>
        <div className="demo-arrow-wrap">
          <div className="demo-arrow-line" />
          <span className="demo-arrow-icon">🔒</span>
        </div>
        <div className="demo-pane">
          <p className="demo-pane-label">Encrypted Output</p>
          <p className="demo-pane-value cipher">{demo.cipher}</p>
        </div>
      </div>
      <div className="demo-indicator">
        {DEMOS.map((_, i) => (
          <span key={i} className={`demo-dot ${i === idx ? "active" : ""}`} />
        ))}
      </div>
    </div>
  );
}

function Home() {
  return (
    <div className="home">
      {/* ── Hero ── */}
      <section className="hero">
        <div className="hero-glow hero-glow-1" />
        <div className="hero-glow hero-glow-2" />
        <div className="hero-grid-overlay" />
        <div className="hero-content">
          <div className="hero-badge">
            <span className="hero-badge-dot" />
            Bank-grade Encryption · Free to use
          </div>
          <h1 className="hero-title">
            <span className="gradient-text">Encrypt & Decrypt</span>
            <br />
            with Confidence
          </h1>
          <p className="hero-subtitle">
            Military-grade encryption at your fingertips. Protect text, send
            secure emails, and export encrypted files — all from one dashboard.
          </p>
          <div className="hero-cta">
            <Link to="/register" className="btn btn-primary btn-lg hero-btn-glow">
              Get Started Free →
            </Link>
            <Link to="/login" className="btn btn-outline btn-lg">
              Sign In
            </Link>
          </div>
          <div className="hero-trust">
            {ALGORITHMS.map((a) => (
              <span key={a} className="trust-chip">{a}</span>
            ))}
          </div>
        </div>
      </section>

      {/* ── Live Demo ── */}
      <section className="demo-section">
        <p className="section-eyebrow">See it in action</p>
        <h2 className="section-title">Watch encryption happen in real time</h2>
        <p className="section-sub">
          Your plain text is transformed into an unreadable cipher — only the right key unlocks it.
        </p>
        <LiveDemo />
      </section>

      {/* ── Stats ── */}
      <section className="stats-strip">
        {STATS.map((s) => (
          <div key={s.label} className="stat-item">
            <span className="stat-num">{s.value}</span>
            <span className="stat-lbl">{s.label}</span>
          </div>
        ))}
      </section>

      {/* ── Features ── */}
      <section className="features-section">
        <p className="section-eyebrow">Everything you need</p>
        <h2 className="section-title">One platform, all the tools</h2>
        <div className="features-grid">
          <div className="feature-card">
            <div className="feature-card-icon">🔐</div>
            <h3>Text Encryption</h3>
            <p>
              Encrypt any text instantly with AES-128, AES-256, Triple-DES, or
              RSA-2048. Copy the cipher with one click.
            </p>
          </div>
          <div className="feature-card">
            <div className="feature-card-icon">📧</div>
            <h3>Secure Email</h3>
            <p>
              Send encrypted messages directly via email using Gmail SMTP. The
              recipient decrypts using your shared algorithm.
            </p>
          </div>
          <div className="feature-card">
            <div className="feature-card-icon">📁</div>
            <h3>File Export</h3>
            <p>
              Export all your encrypted records as a <code>.enc</code> file.
              Re-import anytime to restore as a <code>.xlsx</code> spreadsheet.
            </p>
          </div>
          <div className="feature-card">
            <div className="feature-card-icon">📊</div>
            <h3>Reports & History</h3>
            <p>
              View your full operation history, file records, and a summary of
              all encryption activities in one place.
            </p>
          </div>
        </div>
      </section>

      {/* ── How it works ── */}
      <section className="how-section">
        <p className="section-eyebrow">Simple process</p>
        <h2 className="section-title">Up and running in minutes</h2>
        <div className="steps">
          {STEPS.map((step, i) => (
            <div key={step.num} className="step-card">
              <div className="step-num">{step.num}</div>
              <div className="step-icon">{step.icon}</div>
              <h3>{step.title}</h3>
              <p>{step.desc}</p>
              {i < STEPS.length - 1 && <div className="step-connector" />}
            </div>
          ))}
        </div>
      </section>

      {/* ── Bottom CTA ── */}
      <section className="cta-banner">
        <div className="cta-glow" />
        <h2>Ready to secure your data?</h2>
        <p>Join CipherVault and encrypt your first message in under 60 seconds.</p>
        <Link to="/register" className="btn btn-primary btn-lg hero-btn-glow">
          Start for Free →
        </Link>
      </section>
    </div>
  );
}

export default Home;
