import { useEffect, useMemo, useState } from "react";
import "./dark-mode.css";
import {
  Activity,
  AlertTriangle,
  ArrowDownRight,
  ArrowLeftRight,
  ArrowUpRight,
  Bell,
  BrainCircuit,
  CheckCircle2,
  ChevronDown,
  CreditCard,
  GitCompare,
  Landmark,
  LayoutDashboard,
  Menu,
  RefreshCw,
  Search,
  Settings,
  Sun,
  Moon,
  ShieldCheck,
  ShieldAlert,
  Users,
  X,
  Lock,
  Mail,
  UserRound,
} from "lucide-react";
import "./App.css";

const API_BASE_URL = "http://localhost:8080";

const navigation = [
  {
    label: "MAIN",
    items: [{ name: "Overview", icon: LayoutDashboard }],
  },
  {
    label: "MONITOR",
    items: [
      { name: "Risk Intelligence", icon: ShieldCheck },
      { name: "Revenue Recovery", icon: RefreshCw },
      { name: "Finance Control", icon: Landmark },
    ],
  },
  {
    label: "ANALYTICS",
    items: [
      { name: "Transactions", icon: ArrowLeftRight },
      { name: "Customers", icon: Users },
      { name: "Payment Methods", icon: CreditCard },
    ],
  },
  {
    label: "OPERATIONS",
    items: [
      { name: "Alerts", icon: Bell },
      { name: "Recovery Actions", icon: Activity },
      { name: "Reconciliation", icon: GitCompare },
    ],
  },
  {
    label: "SYSTEM",
    items: [
      { name: "AI Decisions", icon: BrainCircuit },
      { name: "Settings", icon: Settings },
    ],
  },
];

function formatNumber(value) {
  return new Intl.NumberFormat("en-IN").format(Number(value || 0));
}

function formatCurrency(value) {
  return new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  }).format(Number(value || 0));
}

function formatCompactCurrency(value) {
  const amount = Number(value || 0);

  if (amount >= 10000000) {
    return `₹${(amount / 10000000).toFixed(2)}Cr`;
  }

  if (amount >= 100000) {
    return `₹${(amount / 100000).toFixed(2)}L`;
  }

  if (amount >= 1000) {
    return `₹${(amount / 1000).toFixed(1)}K`;
  }

  return formatCurrency(amount);
}

function AuthPage({ mode, onSuccess }) {
  const isSignup = mode === "signup";
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const goTo = (path) => {
    window.history.pushState({}, "", path);
    window.dispatchEvent(new PopStateEvent("popstate"));
  };

  const submit = (event) => {
    event.preventDefault();
    setError("");

    if (isSignup && !name.trim()) {
      setError("Please enter your name.");
      return;
    }
    if (!email.trim() || !email.includes("@")) {
      setError("Please enter a valid email address.");
      return;
    }
    if (password.length < 6) {
      setError("Password must be at least 6 characters.");
      return;
    }

    const account = {
      name: name.trim() || "Merchant Admin",
      email: email.trim().toLowerCase(),
      password,
    };

    if (isSignup) {
      localStorage.setItem("merchant-demo-account", JSON.stringify(account));
    } else {
      const saved = JSON.parse(localStorage.getItem("merchant-demo-account") || "null");
      if (saved && (saved.email !== account.email || saved.password !== account.password)) {
        setError("Email or password is incorrect.");
        return;
      }
    }

    localStorage.setItem("merchant-authenticated", "true");
    localStorage.setItem("merchant-user", JSON.stringify({
      name: account.name,
      email: account.email,
    }));
    onSuccess();
  };

  return (
    <div className="auth-shell">
      <div className="auth-brand">
        <div className="brand-mark">PI</div>
        <div>
          <div className="auth-brand-name">Payment Intelligence</div>
          <div className="auth-brand-subtitle">Merchant Control Center</div>
        </div>
      </div>

      <div className="auth-content">
        <div className="auth-card">
          <div className="auth-heading">
            <div className="auth-icon"><Lock size={20} /></div>
            <div className="eyebrow">MERCHANT PORTAL</div>
            <h1>{isSignup ? "Create your account" : "Welcome back"}</h1>
            <p>{isSignup ? "Set up your merchant workspace to start using Payment Intelligence." : "Sign in to access your merchant intelligence dashboard."}</p>
          </div>

          <form className="auth-form" onSubmit={submit}>
            {isSignup && (
              <label>
                Full name
                <div className="auth-input-wrap">
                  <UserRound size={17} />
                  <input value={name} onChange={(e) => setName(e.target.value)} placeholder="Merchant Admin" autoComplete="name" />
                </div>
              </label>
            )}

            <label>
              Email address
              <div className="auth-input-wrap">
                <Mail size={17} />
                <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} placeholder="you@business.com" autoComplete="email" />
              </div>
            </label>

            <label>
              Password
              <div className="auth-input-wrap">
                <Lock size={17} />
                <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} placeholder="••••••••" autoComplete={isSignup ? "new-password" : "current-password"} />
              </div>
            </label>

            {error && <div className="auth-error">{error}</div>}

            <button className="auth-submit" type="submit">
              {isSignup ? "Create account" : "Sign in"}
            </button>
          </form>

          <div className="auth-switch">
            {isSignup ? "Already have an account?" : "Don't have an account?"}{" "}
            <button type="button" onClick={() => goTo(isSignup ? "/login" : "/signup")}>
              {isSignup ? "Sign in" : "Create account"}
            </button>
          </div>

          <div className="auth-note">Secure merchant workspace • Payment Intelligence</div>
        </div>
      </div>
    </div>
  );
}

function App() {
  const [authPath, setAuthPath] = useState(window.location.pathname === "/signup" ? "signup" : window.location.pathname === "/login" ? "login" : null);
  const [authenticated, setAuthenticated] = useState(() => localStorage.getItem("merchant-authenticated") === "true");

  useEffect(() => {
    const handlePopState = () => {
      const path = window.location.pathname;
      setAuthPath(path === "/signup" ? "signup" : path === "/login" ? "login" : null);
    };
    window.addEventListener("popstate", handlePopState);
    return () => window.removeEventListener("popstate", handlePopState);
  }, []);

  const completeAuth = () => {
    window.history.replaceState({}, "", "/");
    setAuthPath(null);
    setAuthenticated(true);
  };

  const [activePage, setActivePage] = useState("Overview");
  const [sidebarOpen, setSidebarOpen] = useState(false);
const [darkMode, setDarkMode] = useState(() => {
  return localStorage.getItem("merchant-dark-mode") === "true";
});

  useEffect(() => {
    document.documentElement.classList.toggle("dark-mode", darkMode);
    localStorage.setItem("merchant-dark-mode", String(darkMode));
  }, [darkMode]);
  const [notificationPrefs, setNotificationPrefs] = useState(() => JSON.parse(localStorage.getItem("merchant-notifications") || "true"));
  const [aiPrefs, setAiPrefs] = useState(() => JSON.parse(localStorage.getItem("merchant-ai-insights") || "true"));

  const toggleNotificationPrefs = () => {
    setNotificationPrefs((value) => {
      const next = !value;
      localStorage.setItem("merchant-notifications", JSON.stringify(next));
      return next;
    });
  };

  const toggleAiPrefs = () => {
    setAiPrefs((value) => {
      const next = !value;
      localStorage.setItem("merchant-ai-insights", JSON.stringify(next));
      return next;
    });
  };
  const [transactions, setTransactions] = useState([]);
  const [transactionsLoading, setTransactionsLoading] = useState(true);
  const [transactionsError, setTransactionsError] = useState("");

  const [riskAnalytics, setRiskAnalytics] = useState(null);
  const [riskLoading, setRiskLoading] = useState(true);
  const [riskError, setRiskError] = useState("");

  const [selectedTransactionId, setSelectedTransactionId] =
    useState("");

  const [riskSignals, setRiskSignals] = useState(null);
  const [riskSignalsLoading, setRiskSignalsLoading] =
    useState(false);
  const [riskSignalsError, setRiskSignalsError] = useState("");

  const [businessInsight, setBusinessInsight] = useState(null);
  const [businessInsightLoading, setBusinessInsightLoading] =
    useState(true);
  const [businessInsightError, setBusinessInsightError] =
    useState("");

  // =========================================================
  // REVENUE RECOVERY STATE
  // =========================================================
  const [recoveryOpportunity, setRecoveryOpportunity] =
    useState(null);
  const [recoveryLoading, setRecoveryLoading] =
    useState(true);
  const [recoveryError, setRecoveryError] =
    useState("");
  const [recoveryDecision, setRecoveryDecision] =
    useState(null);
  const [recoveryDecisionLoading, setRecoveryDecisionLoading] =
    useState(false);
  const [recoveryDecisionError, setRecoveryDecisionError] =
    useState("");
  const [recoveryExecution, setRecoveryExecution] =
    useState(null);
  const [recoveryExecuting, setRecoveryExecuting] =
    useState(false);
  const [recoveryExecutionError, setRecoveryExecutionError] =
    useState("");

  // =========================================================
  // FINANCE CONTROL STATE
  // =========================================================
  const [revenueAnalytics, setRevenueAnalytics] = useState(null);
  const [revenueAnalyticsLoading, setRevenueAnalyticsLoading] =
    useState(true);
  const [revenueAnalyticsError, setRevenueAnalyticsError] =
    useState("");

  const [search, setSearch] = useState("");
  const [selectedTransaction, setSelectedTransaction] =
    useState(null);

  /*
   * =========================================================
   * LOAD TRANSACTIONS
   * =========================================================
   */

  useEffect(() => {
    const loadTransactions = async () => {
      try {
        setTransactionsLoading(true);
        setTransactionsError("");

        const response = await fetch(
          `${API_BASE_URL}/api/transactions`
        );

        if (!response.ok) {
          throw new Error(
            `Transaction request failed: ${response.status}`
          );
        }

        const data = await response.json();

        if (!Array.isArray(data)) {
          throw new Error(
            "Backend returned an invalid transaction response."
          );
        }

        setTransactions(data);

        if (data.length > 0) {
          setSelectedTransactionId(data[0].transactionId);
        }
      } catch (error) {
        console.error(error);
        setTransactionsError(
          "Unable to load transactions from backend."
        );
      } finally {
        setTransactionsLoading(false);
      }
    };

    loadTransactions();
  }, []);

  /*
   * =========================================================
   * LOAD RISK ANALYTICS
   * =========================================================
   */

  useEffect(() => {
    const loadRiskAnalytics = async () => {
      try {
        setRiskLoading(true);
        setRiskError("");

        const response = await fetch(
          `${API_BASE_URL}/api/risk/analytics`
        );

        if (!response.ok) {
          throw new Error(
            `Risk analytics request failed: ${response.status}`
          );
        }

        const data = await response.json();

        setRiskAnalytics(data);
      } catch (error) {
        console.error(error);
        setRiskError(
          "Unable to load risk analytics from backend."
        );
      } finally {
        setRiskLoading(false);
      }
    };

    loadRiskAnalytics();
  }, []);

  /*
   * =========================================================
   * LOAD BUSINESS INTELLIGENCE
   * =========================================================
   *
   * Uses the verified endpoint:
   * POST /api/v1/analytics/business-insight
   *
   * The endpoint expects an array of TransactionInput objects.
   */

  useEffect(() => {
    const loadBusinessInsight = async () => {
      try {
        setBusinessInsightLoading(true);
        setBusinessInsightError("");

        if (!Array.isArray(transactions)) {
          return;
        }

        const response = await fetch(
          `${API_BASE_URL}/api/v1/analytics/business-insight`,
          {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
            },
            body: JSON.stringify(transactions),
          }
        );

        if (!response.ok) {
          throw new Error(
            `Business insight request failed: ${response.status}`
          );
        }

        const data = await response.json();

        setBusinessInsight(data);
      } catch (error) {
        console.error(error);

        setBusinessInsight(null);
        setBusinessInsightError(
          "Unable to load business intelligence."
        );
      } finally {
        setBusinessInsightLoading(false);
      }
    };

    /*
     * Don't call the API until transactions have loaded.
     */
    if (!transactionsLoading) {
      loadBusinessInsight();
    }
  }, [transactions, transactionsLoading]);

  /*
   * =========================================================
   * LOAD RECOVERY OPPORTUNITY
   * =========================================================
   */

  useEffect(() => {
    const loadRecoveryOpportunity = async () => {
      try {
        setRecoveryLoading(true);
        setRecoveryError("");

        const response = await fetch(
          `${API_BASE_URL}/api/recovery/opportunity`
        );

        if (!response.ok) {
          throw new Error(
            `Recovery opportunity request failed: ${response.status}`
          );
        }

        const data = await response.json();
        setRecoveryOpportunity(data);
      } catch (error) {
        console.error(error);
        setRecoveryOpportunity(null);
        setRecoveryError(
          "Unable to load recovery opportunity."
        );
      } finally {
        setRecoveryLoading(false);
      }
    };

    loadRecoveryOpportunity();
  }, []);

  /*
   * =========================================================
   * LOAD FINANCE CONTROL ANALYTICS
   * =========================================================
   *
   * GET /api/analytics/revenue is the existing backend revenue
   * analytics endpoint. It is loaded from the real transaction DB.
   */

  const loadRevenueAnalytics = async () => {
    try {
      setRevenueAnalyticsLoading(true);
      setRevenueAnalyticsError("");

      const response = await fetch(
        `${API_BASE_URL}/api/analytics/revenue`
      );

      if (!response.ok) {
        throw new Error(
          `Revenue analytics request failed: ${response.status}`
        );
      }

      const data = await response.json();
      setRevenueAnalytics(data);
    } catch (error) {
      console.error(error);
      setRevenueAnalytics(null);
      setRevenueAnalyticsError(
        "Unable to load finance control analytics."
      );
    } finally {
      setRevenueAnalyticsLoading(false);
    }
  };

  useEffect(() => {
    loadRevenueAnalytics();
  }, []);

  /*
   * =========================================================
   * RECOVERY DECISION
   * =========================================================
   */

  const loadRecoveryDecision = async (transactionId) => {
    if (!transactionId) return;

    try {
      setRecoveryDecisionLoading(true);
      setRecoveryDecisionError("");
      setRecoveryExecution(null);
      setRecoveryExecutionError("");

      const response = await fetch(
        `${API_BASE_URL}/api/recovery/decision/${encodeURIComponent(
          transactionId
        )}`
      );

      if (!response.ok) {
        throw new Error(
          `Recovery decision request failed: ${response.status}`
        );
      }

      const data = await response.json();
      setRecoveryDecision(data);
    } catch (error) {
      console.error(error);
      setRecoveryDecision(null);
      setRecoveryDecisionError(
        "Unable to load recovery decision."
      );
    } finally {
      setRecoveryDecisionLoading(false);
    }
  };

  /*
   * =========================================================
   * EXECUTE RECOVERY SIMULATION
   * =========================================================
   */

  const executeRecovery = async () => {
    const transactionId = recoveryDecision?.transactionId;

    if (!transactionId) return;

    try {
      setRecoveryExecuting(true);
      setRecoveryExecutionError("");
      setRecoveryExecution(null);

      const response = await fetch(
        `${API_BASE_URL}/api/recovery/execute/${encodeURIComponent(
          transactionId
        )}`,
        { method: "POST" }
      );

      if (!response.ok) {
        const message = await response.text();
        throw new Error(
          message || `Recovery execution failed: ${response.status}`
        );
      }

      const data = await response.json();
      setRecoveryExecution(data);

      // Refresh transactions because execution can change FAILED -> SUCCESS.
      const transactionsResponse = await fetch(
        `${API_BASE_URL}/api/transactions`
      );

      if (transactionsResponse.ok) {
        const transactionData = await transactionsResponse.json();
        if (Array.isArray(transactionData)) {
          setTransactions(transactionData);
        }
      }

      // Refresh recovery totals after execution.
      const opportunityResponse = await fetch(
        `${API_BASE_URL}/api/recovery/opportunity`
      );

      if (opportunityResponse.ok) {
        const opportunityData = await opportunityResponse.json();
        setRecoveryOpportunity(opportunityData);
      }

      // Refresh decision so the UI reflects the transaction's new state.
      const decisionResponse = await fetch(
        `${API_BASE_URL}/api/recovery/decision/${encodeURIComponent(
          transactionId
        )}`
      );

      if (decisionResponse.ok) {
        const decisionData = await decisionResponse.json();
        setRecoveryDecision(decisionData);
      }
    } catch (error) {
      console.error(error);
      setRecoveryExecutionError(
        error.message || "Unable to execute recovery."
      );
    } finally {
      setRecoveryExecuting(false);
    }
  };

  /*
   * =========================================================
   * LOAD RISK SIGNALS
   * =========================================================
   */

  useEffect(() => {
    if (!selectedTransactionId) {
      setRiskSignals(null);
      return;
    }

    const loadRiskSignals = async () => {
      try {
        setRiskSignalsLoading(true);
        setRiskSignalsError("");

        const response = await fetch(
          `${API_BASE_URL}/api/risk/signals/${encodeURIComponent(
            selectedTransactionId
          )}`
        );

        if (!response.ok) {
          throw new Error(
            `Risk signal request failed: ${response.status}`
          );
        }

        const data = await response.json();

        setRiskSignals(data);
      } catch (error) {
        console.error(error);

        setRiskSignals(null);
        setRiskSignalsError(
          `Unable to load risk analysis for ${selectedTransactionId}.`
        );
      } finally {
        setRiskSignalsLoading(false);
      }
    };

    loadRiskSignals();
  }, [selectedTransactionId]);

  /*
   * =========================================================
   * SELECTED TRANSACTION
   * =========================================================
   */

  useEffect(() => {
    const transaction = transactions.find(
      (item) => item.transactionId === selectedTransactionId
    );

    setSelectedTransaction(transaction || null);
  }, [transactions, selectedTransactionId]);

  /*
   * =========================================================
   * SEARCH
   * =========================================================
   */

  const filteredTransactions = useMemo(() => {
    const query = search.trim().toLowerCase();

    if (!query) {
      return transactions;
    }

    return transactions.filter((transaction) =>
      [
        transaction.transactionId,
        transaction.customerId,
        transaction.merchantId,
        transaction.paymentMethod,
        transaction.status,
        transaction.failureReason,
      ]
        .filter(Boolean)
        .join(" ")
        .toLowerCase()
        .includes(query)
    );
  }, [transactions, search]);

  /*
   * =========================================================
   * RISK DISTRIBUTION
   * =========================================================
   */

  const riskData = useMemo(() => {
    const total =
      Number(riskAnalytics?.totalTransactions) ||
      transactions.length ||
      0;

    const backendBreakdown =
      riskAnalytics?.riskBreakdown || [];

    return ["LOW", "MEDIUM", "HIGH"].map((level) => {
      const item = backendBreakdown.find(
        (risk) => risk.riskLevel === level
      );

      const count = Number(item?.transactionCount || 0);

      const percentage =
        total > 0
          ? Math.round((count / total) * 100)
          : 0;

      const tone = {
        LOW: "safe",
        MEDIUM: "warning",
        HIGH: "danger",
      };

      return {
        label:
          level.charAt(0) +
          level.slice(1).toLowerCase(),
        value: percentage,
        count,
        tone: tone[level],
      };
    });
  }, [riskAnalytics, transactions.length]);

  /*
   * =========================================================
   * TRANSACTION METRICS
   * =========================================================
   */

  const transactionMetrics = useMemo(() => {
    const total = transactions.length;

    const successful = transactions.filter(
      (transaction) =>
        transaction.status === "SUCCESS"
    );

    const failed = transactions.filter(
      (transaction) =>
        transaction.status === "FAILED"
    );

    const totalProcessed = transactions.reduce(
      (sum, transaction) =>
        sum + Number(transaction.amount || 0),
      0
    );

    const failedValue = failed.reduce(
      (sum, transaction) =>
        sum + Number(transaction.amount || 0),
      0
    );

    const successValue = successful.reduce(
      (sum, transaction) =>
        sum + Number(transaction.amount || 0),
      0
    );

    const successRate =
      total > 0
        ? (successful.length / total) * 100
        : 0;

    const failureRate =
      total > 0
        ? (failed.length / total) * 100
        : 0;

    return {
      total,
      successful: successful.length,
      failed: failed.length,
      totalProcessed,
      failedValue,
      successValue,
      successRate,
      failureRate,
    };
  }, [transactions]);

  /*
   * =========================================================
   * PRIORITY OPERATIONS
   * =========================================================
   */

  const priorityTransactions = useMemo(() => {
    return [...transactions]
      .filter(
        (transaction) =>
          transaction.status === "FAILED"
      )
      .sort(
        (a, b) =>
          Number(b.amount || 0) -
          Number(a.amount || 0)
      )
      .slice(0, 5);
  }, [transactions]);

  /*
   * =========================================================
   * PAYMENT TREND
   * =========================================================
   */

  const paymentTrend = useMemo(() => {
    const buckets = Array(24).fill(0);

    transactions.forEach((transaction) => {
      if (!transaction.createdAt) {
        return;
      }

      const date = new Date(transaction.createdAt);

      if (Number.isNaN(date.getTime())) {
        return;
      }

      const hour = date.getHours();

      if (hour >= 0 && hour < 24) {
        buckets[hour] += Number(
          transaction.amount || 0
        );
      }
    });

    const max = Math.max(...buckets, 1);

    return buckets.map(
      (value) => (value / max) * 100
    );
  }, [transactions]);

  /*
   * =========================================================
   * BELOW HIGH RISK
   * =========================================================
   */

  const belowHighRiskPercentage = useMemo(() => {
    const total =
      Number(riskAnalytics?.totalTransactions) ||
      0;

    const high =
      Number(
        riskAnalytics?.highRiskTransactions
      ) || 0;

    if (total === 0) {
      return 0;
    }

    return Math.round(
      ((total - high) / total) * 100
    );
  }, [riskAnalytics]);

  /*
   * =========================================================
   * NAVIGATION
   * =========================================================
   */

  const navigate = (page) => {
    setActivePage(page);
    setSidebarOpen(false);
  };

  /*
   * =========================================================
   * RENDER
   * =========================================================
   */

  if (!authenticated || authPath) {
    return <AuthPage mode={authPath || "login"} onSuccess={completeAuth} />;
  }

  return (
    <div className="app-shell">
      {sidebarOpen && (
        <button
          className="sidebar-overlay"
          onClick={() => setSidebarOpen(false)}
          aria-label="Close navigation"
        />
      )}

      <aside
        className={`sidebar ${
          sidebarOpen ? "sidebar-open" : ""
        }`}
      >
        <div className="brand">
          <div className="brand-mark">PI</div>

          <div className="brand-copy">
            <div className="brand-name">
              Payment Intelligence
            </div>

            <div className="brand-subtitle">
              Merchant Control Center
            </div>
          </div>

          <button
            className="sidebar-close"
            onClick={() => setSidebarOpen(false)}
            aria-label="Close sidebar"
          >
            <X size={17} />
          </button>
        </div>

        <nav className="navigation">
          {navigation.map((section) => (
            <div
              className="nav-section"
              key={section.label}
            >
              <div className="nav-section-label">
                {section.label}
              </div>

              {section.items.map((item) => {
                const Icon = item.icon;

                const active =
                  activePage === item.name;

                return (
                  <button
                    key={item.name}
                    className={`nav-item ${
                      active ? "active" : ""
                    }`}
                    onClick={() =>
                      navigate(item.name)
                    }
                  >
                    <Icon
                      size={16}
                      strokeWidth={1.8}
                    />

                    <span>{item.name}</span>
                  </button>
                );
              })}
            </div>
          ))}
        </nav>

        <div className="system-status">
          <CheckCircle2 size={16} />

          <div>
            <div className="status-title">
              System Operational
            </div>

            <div className="status-subtitle">
              Backend connected
            </div>
          </div>
        </div>
      </aside>

      <div className="main-wrapper">
        <header className="topbar">
          <div className="topbar-left">
            <button
              className="mobile-menu"
              onClick={() => setSidebarOpen(true)}
              aria-label="Open navigation"
            >
              <Menu size={20} />
            </button>

            <div>
              <div className="breadcrumb">
                Payment Intelligence
              </div>

              <h1>{activePage}</h1>
            </div>
          </div>

          <div className="topbar-actions">
            <div className="search-box">
              <Search size={16} />

              <input
                value={search}
                onChange={(event) =>
                  setSearch(event.target.value)
                }
                placeholder="Search transactions..."
                aria-label="Search transactions"
              />

              <span className="search-shortcut">
                Ctrl K
              </span>
            </div>

            <button className="merchant-selector">
              <span className="merchant-dot" />
              <span>All Merchants</span>
              <ChevronDown size={14} />
            </button>

            <button
              className="icon-button"
              aria-label="Notifications"
            >
              <Bell size={17} />
              <span className="notification-dot" />
            </button>

            <button className="profile-button">
              <div className="avatar">UR</div>

              <div className="profile-info">
                <span className="profile-name">
                  Merchant Admin
                </span>

                <span className="profile-role">
                  Administrator
                </span>
              </div>

              <ChevronDown size={14} />
            </button>
          </div>
        </header>

        <main className="page-content">
          {activePage === "Overview" ? (
            <>
              <section className="page-heading">
                <div>
                  <div className="eyebrow">
                    MERCHANT OPERATIONS
                  </div>

                  <h2>
                    Payment Intelligence
                  </h2>

                  <p>
                    Monitor payment risk,
                    analyze transaction
                    behavior and identify
                    operational risk.
                  </p>
                </div>

                <div className="last-updated">
                  <span className="live-indicator" />
                  Live backend data
                </div>
              </section>

              {/* =================================================
                  REAL TRANSACTION KPIs
              ================================================= */}

              <section className="kpi-grid">
                <MetricCard
                  label="Transactions"
                  value={formatNumber(
                    transactionMetrics.total
                  )}
                  note="loaded from backend"
                  positive
                />

                <MetricCard
                  label="Processed Value"
                  value={formatCompactCurrency(
                    transactionMetrics.totalProcessed
                  )}
                  note="transaction dataset"
                  positive
                />

                <MetricCard
                  label="Successful"
                  value={formatNumber(
                    transactionMetrics.successful
                  )}
                  note={`${transactionMetrics.successRate.toFixed(
                    1
                  )}% success rate`}
                  positive
                />

                <MetricCard
                  label="Failed"
                  value={formatNumber(
                    transactionMetrics.failed
                  )}
                  note={`${transactionMetrics.failureRate.toFixed(
                    1
                  )}% failure rate`}
                />

                <MetricCard
                  label="Failed Value"
                  value={formatCompactCurrency(
                    transactionMetrics.failedValue
                  )}
                  note="value currently failed"
                />

                <MetricCard
                  label="Risk Analyzed"
                  value={
                    riskLoading
                      ? "—"
                      : formatNumber(
                          riskAnalytics?.totalTransactions
                        )
                  }
                  note="backend risk analytics"
                  positive
                />
              </section>

              {/* =================================================
                  BUSINESS INTELLIGENCE
              ================================================= */}

              <section className="panel business-intelligence-panel">
                <PanelHeader
                  title="Business Intelligence"
                  subtitle="Backend-generated merchant performance summary"
                  right={
                    <span className="period-label">
                      Live analytics
                    </span>
                  }
                />

                {businessInsightLoading ? (
                  <div className="analysis-empty">
                    Loading business intelligence...
                  </div>
                ) : businessInsightError ? (
                  <div className="analysis-error">
                    <AlertTriangle size={17} />
                    <span>
                      {businessInsightError}
                    </span>
                  </div>
                ) : businessInsight ? (
                  <>
                    <div className="business-summary-grid">
                      <BusinessInsightCard
                        label="Revenue"
                        value={formatCompactCurrency(
                          businessInsight.overview?.revenue
                        )}
                        note="successful payment revenue"
                      />

                      <BusinessInsightCard
                        label="Revenue Lost"
                        value={formatCompactCurrency(
                          businessInsight.overview?.revenueLost
                        )}
                        note="failed payment value"
                        danger
                      />

                      <BusinessInsightCard
                        label="Success Rate"
                        value={`${Number(
                          businessInsight.overview
                            ?.successRate || 0
                        ).toFixed(1)}%`}
                        note={`${formatNumber(
                          businessInsight.overview
                            ?.successfulTransactions
                        )} successful`}
                      />

                      <BusinessInsightCard
                        label="Failure Rate"
                        value={`${Number(
                          businessInsight.overview
                            ?.failureRate || 0
                        ).toFixed(1)}%`}
                        note={`${formatNumber(
                          businessInsight.overview
                            ?.failedTransactions
                        )} failed`}
                        danger
                      />
                    </div>

                    <div className="business-detail-grid">
                      <BusinessDetail
                        label="Most Used Payment Method"
                        value={
                          businessInsight.mostUsedPaymentMethod ||
                          "N/A"
                        }
                      />

                      <BusinessDetail
                        label="Top Failure Reason"
                        value={
                          businessInsight.topFailureReason ||
                          "N/A"
                        }
                      />

                      <BusinessDetail
                        label="Highest Revenue Loss"
                        value={
                          businessInsight.highestRevenueLossPaymentMethod
                            ? `${businessInsight.highestRevenueLossPaymentMethod} · ${formatCurrency(
                                businessInsight.highestMethodRevenueLoss
                              )}`
                            : "N/A"
                        }
                      />

                      <BusinessDetail
                        label="Peak Sales Hours"
                        value={
                          Array.isArray(
                            businessInsight.peakSalesHours
                          ) &&
                          businessInsight.peakSalesHours
                            .length > 0
                            ? businessInsight.peakSalesHours.join(
                                ", "
                              )
                            : "N/A"
                        }
                      />
                    </div>

                    <div className="business-lists">
                      <div className="business-list-section">
                        <div className="drawer-section-title">
                          Business Insights
                        </div>

                        <div className="business-list">
                          {Array.isArray(
                            businessInsight.insights
                          ) &&
                          businessInsight.insights.length >
                            0 ? (
                            businessInsight.insights.map(
                              (insight, index) => (
                                <div
                                  className="business-list-item"
                                  key={`${index}-${insight}`}
                                >
                                  <CheckCircle2
                                    size={15}
                                  />
                                  <span>
                                    {insight}
                                  </span>
                                </div>
                              )
                            )
                          ) : (
                            <div className="analysis-empty">
                              No insights available.
                            </div>
                          )}
                        </div>
                      </div>

                      <div className="business-list-section">
                        <div className="drawer-section-title">
                          Recommendations
                        </div>

                        <div className="business-list">
                          {Array.isArray(
                            businessInsight.recommendations
                          ) &&
                          businessInsight
                            .recommendations.length >
                            0 ? (
                            businessInsight.recommendations.map(
                              (
                                recommendation,
                                index
                              ) => (
                                <div
                                  className="business-list-item recommendation"
                                  key={`${index}-${recommendation}`}
                                >
                                  <ArrowUpRight
                                    size={15}
                                  />
                                  <span>
                                    {
                                      recommendation
                                    }
                                  </span>
                                </div>
                              )
                            )
                          ) : (
                            <div className="analysis-empty">
                              No recommendations available.
                            </div>
                          )}
                        </div>
                      </div>
                    </div>
                  </>
                ) : (
                  <div className="analysis-empty">
                    Business intelligence is unavailable.
                  </div>
                )}
              </section>

              {/* =================================================
                  PAYMENT VOLUME
              ================================================= */}

              <section className="dashboard-grid">
                <div className="panel trend-panel">
                  <PanelHeader
                    title="Payment volume"
                    subtitle="Transaction value by hour"
                    right={
                      <span className="period-label">
                        Database transactions
                      </span>
                    }
                  />

                  <div className="trend-summary">
                    <div>
                      <span className="summary-label">
                        Current dataset value
                      </span>

                      <strong>
                        {formatCompactCurrency(
                          transactionMetrics.totalProcessed
                        )}
                      </strong>
                    </div>

                    <div className="trend-change positive">
                      <Activity size={15} />
                      Live
                    </div>
                  </div>

                  <div className="chart">
                    <div className="chart-grid">
                      <span />
                      <span />
                      <span />
                      <span />
                    </div>

                    <svg
                      viewBox="0 0 900 260"
                      preserveAspectRatio="none"
                      className="line-chart"
                      role="img"
                      aria-label="Payment volume trend"
                    >
                      <polyline
                        points={paymentTrend
                          .map((value, index) => {
                            const x =
                              (index / 23) *
                                880 +
                              10;

                            const y =
                              230 -
                              value * 1.9;

                            return `${x},${y}`;
                          })
                          .join(" ")}
                        fill="none"
                        stroke="currentColor"
                        strokeWidth="3"
                        vectorEffect="non-scaling-stroke"
                      />
                    </svg>
                  </div>

                  <div className="chart-axis">
                    <span>00:00</span>
                    <span>06:00</span>
                    <span>12:00</span>
                    <span>18:00</span>
                    <span>23:00</span>
                  </div>
                </div>

                {/* =================================================
                    RISK DISTRIBUTION
                ================================================= */}

                <div className="panel risk-panel">
                  <PanelHeader
                    title="Risk distribution"
                    subtitle="Transactions by assessed risk"
                  />

                  <div className="risk-total">
                    {riskLoading ? (
                      <>
                        <strong>—</strong>
                        <span>Loading...</span>
                      </>
                    ) : riskError ? (
                      <>
                        <strong>—</strong>
                        <span>{riskError}</span>
                      </>
                    ) : (
                      <>
                        <strong>
                          {formatNumber(
                            riskAnalytics.totalTransactions
                          )}
                        </strong>

                        <span>
                          transactions analyzed
                        </span>
                      </>
                    )}
                  </div>

                  <div className="risk-bars">
                    {riskData.map((risk) => (
                      <div
                        className="risk-row"
                        key={risk.label}
                      >
                        <div className="risk-row-top">
                          <span>{risk.label}</span>

                          <strong>
                            {risk.value}%{" "}
                            <small>
                              {risk.count}
                            </small>
                          </strong>
                        </div>

                        <div className="risk-track">
                          <div
                            className={`risk-fill ${risk.tone}`}
                            style={{
                              width: `${Math.max(
                                risk.value,
                                risk.count > 0
                                  ? 1
                                  : 0
                              )}%`,
                            }}
                          />
                        </div>
                      </div>
                    ))}
                  </div>

                  <div className="risk-footnote">
                    <ShieldCheck size={15} />

                    <span>
                      {riskAnalytics
                        ? `${belowHighRiskPercentage}% of analyzed transactions are below the high-risk threshold.`
                        : "Risk analysis unavailable."}
                    </span>
                  </div>
                </div>
              </section>

              {/* =================================================
                  TRANSACTION RISK INTELLIGENCE
              ================================================= */}

              <section className="panel risk-analysis-panel">
                <PanelHeader
                  title="Transaction Risk Intelligence"
                  subtitle="Select any transaction from the merchant dataset"
                />

                <div className="risk-analysis-toolbar">
                  <div className="transaction-selector">
                    <label htmlFor="transaction-select">
                      Transaction
                    </label>

                    <select
                      id="transaction-select"
                      value={selectedTransactionId}
                      onChange={(event) =>
                        setSelectedTransactionId(
                          event.target.value
                        )
                      }
                      disabled={
                        transactionsLoading ||
                        transactions.length === 0
                      }
                    >
                      {transactions.map(
                        (transaction) => (
                          <option
                            key={
                              transaction.transactionId
                            }
                            value={
                              transaction.transactionId
                            }
                          >
                            {transaction.transactionId} ·{" "}
                            {formatCurrency(
                              transaction.amount
                            )}{" "}
                            · {transaction.status}
                          </option>
                        )
                      )}
                    </select>
                  </div>

                  {riskSignalsLoading && (
                    <span className="analysis-loading">
                      Analyzing transaction...
                    </span>
                  )}
                </div>

                {transactionsError ? (
                  <div className="analysis-error">
                    <AlertTriangle size={17} />
                    <span>
                      {transactionsError}
                    </span>
                  </div>
                ) : riskSignalsError ? (
                  <div className="analysis-error">
                    <AlertTriangle size={17} />
                    <span>
                      {riskSignalsError}
                    </span>
                  </div>
                ) : riskSignalsLoading ? (
                  <div className="analysis-empty">
                    Loading risk intelligence...
                  </div>
                ) : riskSignals ? (
                  <div className="risk-analysis-content">
                    <div className="risk-score-card">
                      <span>Risk score</span>

                      <strong>
                        {riskSignals.riskScore}
                      </strong>

                      <span
                        className={`status-badge ${String(
                          riskSignals.riskLevel ||
                            "unknown"
                        ).toLowerCase()}`}
                      >
                        {riskSignals.riskLevel}
                      </span>
                    </div>

                    <div className="risk-detail-grid">
                      <RiskDetail
                        label="Transaction"
                        value={
                          riskSignals.transactionId
                        }
                      />

                      <RiskDetail
                        label="Customer"
                        value={
                          riskSignals.customerId
                        }
                      />

                      <RiskDetail
                        label="Merchant"
                        value={
                          riskSignals.merchantId
                        }
                      />

                      <RiskDetail
                        label="Amount"
                        value={formatCurrency(
                          riskSignals.amount
                        )}
                      />

                      <RiskDetail
                        label="Customer transactions"
                        value={formatNumber(
                          riskSignals.customerTransactionCount
                        )}
                      />

                      <RiskDetail
                        label="Customer failures"
                        value={formatNumber(
                          riskSignals.customerFailureCount
                        )}
                      />

                      <RiskDetail
                        label="Customer failure rate"
                        value={`${Number(
                          riskSignals.customerFailureRate ||
                            0
                        ).toFixed(1)}%`}
                      />

                      <RiskDetail
                        label="Merchant average amount"
                        value={formatCurrency(
                          riskSignals.merchantAverageAmount
                        )}
                      />

                      <RiskDetail
                        label="Merchant amount percentile"
                        value={`${Number(
                          riskSignals.merchantAmountPercentile ||
                            0
                        ).toFixed(2)}%`}
                      />
                    </div>

                    <div className="risk-signals-section">
                      <div className="drawer-section-title">
                        Risk signals
                      </div>

                      <div className="risk-signal-list">
                        {Array.isArray(
                          riskSignals.signals
                        ) &&
                        riskSignals.signals.length > 0 ? (
                          riskSignals.signals.map(
                            (signal) => (
                              <span
                                className="risk-signal"
                                key={signal}
                              >
                                <AlertTriangle
                                  size={13}
                                />

                                {String(
                                  signal
                                ).replaceAll(
                                  "_",
                                  " "
                                )}
                              </span>
                            )
                          )
                        ) : (
                          <span className="risk-signal safe-signal">
                            <CheckCircle2
                              size={13}
                            />
                            No elevated risk signals
                          </span>
                        )}
                      </div>
                    </div>
                  </div>
                ) : (
                  <div className="analysis-empty">
                    Select a transaction to calculate
                    its risk intelligence.
                  </div>
                )}
              </section>

              {/* =================================================
                  TRANSACTION TABLE
              ================================================= */}

              <section className="panel recovery-table-panel">
                <PanelHeader
                  title="Transaction intelligence"
                  subtitle="Actual transactions available in the merchant dataset"
                  right={
                    <span className="operations-count">
                      {formatNumber(
                        filteredTransactions.length
                      )}{" "}
                      records
                    </span>
                  }
                />

                <div className="table-wrapper">
                  <table>
                    <thead>
                      <tr>
                        <th>Transaction</th>
                        <th>Amount</th>
                        <th>Merchant</th>
                        <th>Payment method</th>
                        <th>Retry count</th>
                        <th>Status</th>
                      </tr>
                    </thead>

                    <tbody>
                      {filteredTransactions
                        .slice(0, 20)
                        .map((transaction) => (
                          <tr
                            key={
                              transaction.id ||
                              transaction.transactionId
                            }
                            onClick={() =>
                              setSelectedTransactionId(
                                transaction.transactionId
                              )
                            }
                          >
                            <td>
                              <code className="transaction-id">
                                {
                                  transaction.transactionId
                                }
                              </code>
                            </td>

                            <td className="amount-cell">
                              {formatCurrency(
                                transaction.amount
                              )}
                            </td>

                            <td>
                              {transaction.merchantId}
                            </td>

                            <td>
                              {transaction.paymentMethod}
                            </td>

                            <td>
                              {transaction.retryCount ?? 0}
                            </td>

                            <td>
                              <span
                                className={`status-badge ${String(
                                  transaction.status ||
                                    "unknown"
                                ).toLowerCase()}`}
                              >
                                {transaction.status}
                              </span>
                            </td>
                          </tr>
                        ))}
                    </tbody>
                  </table>
                </div>

                {filteredTransactions.length > 20 && (
                  <div className="table-footer">
                    Showing first 20 of{" "}
                    {formatNumber(
                      filteredTransactions.length
                    )}{" "}
                    transactions
                  </div>
                )}
              </section>

              {/* =================================================
                  PRIORITY OPERATIONS
              ================================================= */}

              <section className="panel operations-panel">
                <PanelHeader
                  title="Priority operations"
                  subtitle="Highest-value failed transactions requiring attention"
                  right={
                    <span className="operations-count">
                      {priorityTransactions.length} open
                      items
                    </span>
                  }
                />

                <div className="operations-list">
                  {priorityTransactions.length === 0 ? (
                    <div className="analysis-empty">
                      No failed transactions currently
                      available.
                    </div>
                  ) : (
                    priorityTransactions.map(
                      (transaction) => (
                        <button
                          className="operation-row"
                          key={
                            transaction.transactionId
                          }
                          onClick={() =>
                            setSelectedTransactionId(
                              transaction.transactionId
                            )
                          }
                        >
                          <div className="severity-marker danger" />

                          <div className="operation-main">
                            <strong>
                              Failed transaction requires
                              analysis
                            </strong>

                            <span>
                              <code>
                                {
                                  transaction.transactionId
                                }
                              </code>

                              <span className="dot-separator">
                                ·
                              </span>

                              {transaction.failureReason ||
                                "Failure reason unavailable"}

                              <span className="dot-separator">
                                ·
                              </span>

                              Retry count:{" "}
                              {transaction.retryCount ?? 0}
                            </span>
                          </div>

                          <strong className="operation-amount">
                            {formatCurrency(
                              transaction.amount
                            )}
                          </strong>

                          <span className="status-badge danger">
                            FAILED
                          </span>
                        </button>
                      )
                    )
                  )}
                </div>
              </section>
            </>
          ) : activePage === "Revenue Recovery" ? (
            <RevenueRecoveryPage
              opportunity={recoveryOpportunity}
              loading={recoveryLoading}
              error={recoveryError}
              transactions={transactions}
              decision={recoveryDecision}
              decisionLoading={recoveryDecisionLoading}
              decisionError={recoveryDecisionError}
              execution={recoveryExecution}
              executing={recoveryExecuting}
              executionError={recoveryExecutionError}
              onSelectTransaction={loadRecoveryDecision}
              onExecuteRecovery={executeRecovery}
            />
          ) : activePage === "Finance Control" ? (
            <FinanceControlPage
              analytics={revenueAnalytics}
              loading={revenueAnalyticsLoading}
              error={revenueAnalyticsError}
              transactions={transactions}
              onRefresh={loadRevenueAnalytics}
            />
          ) : activePage === "Risk Intelligence" ? (
            <RiskIntelligencePage
              riskAnalytics={riskAnalytics}
              riskLoading={riskLoading}
              riskError={riskError}
              transactions={transactions}
              onBack={() => navigate("Overview")}
              onRefresh={() => window.location.reload()}
            />
          ) : (
            <FunctionalModulePage
              title={activePage}
              transactions={transactions}
              onSelectTransaction={loadRecoveryDecision}
              onBack={() => navigate("Overview")}
              darkMode={darkMode}
              setDarkMode={setDarkMode}
              notificationPrefs={notificationPrefs}
              toggleNotificationPrefs={toggleNotificationPrefs}
              aiPrefs={aiPrefs}
              toggleAiPrefs={toggleAiPrefs}
            />
          )}
        </main>
      </div>
    </div>
  );
}

function RiskDetail({ label, value }) {
  return (
    <div className="risk-detail-item">
      <span>{label}</span>
      <strong>{value}</strong>
    </div>
  );
}

function BusinessInsightCard({
  label,
  value,
  note,
  danger,
}) {
  return (
    <div
      className={`business-insight-card ${
        danger ? "danger" : ""
      }`}
    >
      <div className="metric-label">{label}</div>

      <div className="metric-value">{value}</div>

      <div className="metric-note">{note}</div>
    </div>
  );
}

function BusinessDetail({ label, value }) {
  return (
    <div className="business-detail-item">
      <span>{label}</span>
      <strong>{value}</strong>
    </div>
  );
}

function MetricCard({
  label,
  value,
  change,
  positive,
  note,
}) {
  return (
    <div className="metric-card">
      <div className="metric-label">
        {label}
      </div>

      <div className="metric-value">
        {value}
      </div>

      <div className="metric-footer">
        {change && (
          <span
            className={`metric-change ${
              positive ? "positive" : "negative"
            }`}
          >
            {positive ? (
              <ArrowUpRight size={13} />
            ) : (
              <ArrowDownRight size={13} />
            )}

            {change}
          </span>
        )}

        <span className="metric-note">
          {note}
        </span>
      </div>
    </div>
  );
}

function PanelHeader({
  title,
  subtitle,
  right,
}) {
  return (
    <div className="panel-header">
      <div>
        <h3>{title}</h3>
        <p>{subtitle}</p>
      </div>

      {right}
    </div>
  );
}

function getModuleDescription(page) {
  const descriptions = {
    "Risk Intelligence": "Monitor transaction-level risk, risk signals and merchant exposure.",
    "Revenue Recovery": "Identify failed payment value and recovery opportunities.",
    "Finance Control": "Monitor revenue, failed value and financial payment performance.",
    Transactions: "Explore merchant transactions and their payment status.",
    Customers: "Understand customer transaction and payment behavior.",
    "Payment Methods": "Compare payment methods and identify payment performance issues.",
    Alerts: "Surface important payment and operational events.",
    "Recovery Actions": "Manage actions associated with failed payment recovery.",
    Reconciliation: "Compare payment records and identify reconciliation issues.",
    "AI Decisions": "Review AI-driven intelligence generated from merchant payment data.",
    Settings: "Manage the configuration of the merchant intelligence workspace.",
  };
  return descriptions[page] || "Merchant intelligence and operational control module.";
}

function getModuleItems(page) {
  const common = {
    "Risk Intelligence": [
      ["Risk Analysis", ShieldCheck, "Assess transaction risk using the existing backend risk engine."],
      ["Risk Signals", AlertTriangle, "Review elevated signals associated with individual transactions."],
      ["Merchant Risk", Landmark, "Understand risk exposure across merchant payment activity."],
    ],
    "Revenue Recovery": [
      ["Recovery Opportunities", RefreshCw, "Review failed transactions that may represent recoverable revenue."],
      ["Revenue at Risk", ArrowDownRight, "Identify payment value currently affected by failed transactions."],
      ["Recovery Performance", Activity, "Track recovery-related operational activity and outcomes."],
    ],
    "Finance Control": [
      ["Revenue Analytics", ArrowUpRight, "Analyze successful payment revenue and transaction value."],
      ["Revenue Loss", ArrowDownRight, "Identify the financial impact of failed payments."],
      ["Payment Performance", CreditCard, "Compare financial performance across payment methods."],
    ],
    Transactions: [
      ["Transaction Dataset", ArrowLeftRight, "Review transactions loaded from the merchant backend."],
      ["Successful Payments", CheckCircle2, "Analyze completed payment activity."],
      ["Failed Payments", AlertTriangle, "Investigate failed transaction activity and failure reasons."],
    ],
    Customers: [
      ["Customer Activity", Users, "Analyze customer transaction activity."],
      ["Customer Failures", AlertTriangle, "Identify customers associated with failed payments."],
      ["Customer Risk", ShieldCheck, "Review customer-level risk indicators."],
    ],
    "Payment Methods": [
      ["Method Usage", CreditCard, "Compare transaction activity across payment methods."],
      ["Failure Performance", AlertTriangle, "Identify payment methods with elevated failures."],
      ["Revenue Loss", ArrowDownRight, "Analyze failed payment value by payment method."],
    ],
    Alerts: [
      ["Payment Alerts", Bell, "Monitor important payment-related events."],
      ["Failure Alerts", AlertTriangle, "Highlight important payment failure events."],
      ["Risk Alerts", ShieldCheck, "Surface transactions requiring risk attention."],
    ],
    "Recovery Actions": [
      ["Recovery Decisions", BrainCircuit, "Review recovery-related decisions and intelligence."],
      ["Recovery Execution", Activity, "Track operational recovery activity."],
      ["Recovery History", RefreshCw, "Review historical recovery attempts."],
    ],
    Reconciliation: [
      ["Transaction Matching", GitCompare, "Compare transaction records for consistency."],
      ["Payment Differences", AlertTriangle, "Identify potential payment discrepancies."],
      ["Financial Control", Landmark, "Review transaction-level financial consistency."],
    ],
    "AI Decisions": [
      ["Risk Decisions", ShieldCheck, "Review risk intelligence generated for transactions."],
      ["Recovery Decisions", RefreshCw, "Review recovery-related intelligence."],
      ["Business Intelligence", BrainCircuit, "Review backend-generated merchant performance insights."],
    ],
    Settings: [
      ["Backend Connection", CheckCircle2, "View the current connection state of the backend API."],
      ["Merchant Configuration", Landmark, "Merchant-specific configuration can be managed here."],
      ["System Preferences", Settings, "Manage workspace and application preferences."],
    ],
  };
  return (common[page] || []).map(([title, icon, description]) => ({ title, icon, description }));
}

function RecoveryMetric({ label, value, note, tone = "" }) {
  return (
    <div className={`metric-card recovery-metric ${tone}`}>
      <div className="metric-label">{label}</div>
      <div className="metric-value">{value}</div>
      <div className="metric-footer">
        <span className="metric-note">{note}</span>
      </div>
    </div>
  );
}

function RecoverySignalList({ title, signals, className = "" }) {
  return (
    <div className={`risk-signals-section ${className}`}>
      <div className="drawer-section-title">{title}</div>
      <div className="risk-signal-list">
        {Array.isArray(signals) && signals.length > 0 ? (
          signals.map((signal) => (
            <span className="risk-signal" key={signal}>
              <CheckCircle2 size={13} />
              {String(signal).replaceAll("_", " ")}
            </span>
          ))
        ) : (
          <span className="risk-signal safe-signal">
            <CheckCircle2 size={13} />
            No signals available
          </span>
        )}
      </div>
    </div>
  );
}

function FinanceControlPage({
  analytics,
  loading,
  error,
  transactions,
  onRefresh,
}) {
  const [period, setPeriod] = useState("MONTH");
  const [customStart, setCustomStart] = useState("");
  const [customEnd, setCustomEnd] = useState("");

  const dateBounds = useMemo(() => {
    const dates = transactions
      .map((t) => new Date(t.createdAt))
      .filter((d) => !Number.isNaN(d.getTime()))
      .sort((a, b) => a - b);

    return {
      min: dates[0] || null,
      max: dates[dates.length - 1] || null,
    };
  }, [transactions]);

  const periodInfo = useMemo(() => {
    const anchor = dateBounds.max || new Date();
    const startOfDay = (d) => new Date(d.getFullYear(), d.getMonth(), d.getDate());
    const addDays = (d, n) => {
      const x = new Date(d);
      x.setDate(x.getDate() + n);
      return x;
    };
    const startOfWeek = (d) => {
      const x = startOfDay(d);
      const day = x.getDay();
      const diff = day === 0 ? 6 : day - 1;
      x.setDate(x.getDate() - diff);
      return x;
    };
    const startOfMonth = (d) => new Date(d.getFullYear(), d.getMonth(), 1);
    const startOfYear = (d) => new Date(d.getFullYear(), 0, 1);

    if (period === "CUSTOM") {
      const start = customStart
        ? new Date(`${customStart}T00:00:00`)
        : startOfMonth(anchor);
      const end = customEnd
        ? new Date(`${customEnd}T23:59:59.999`)
        : anchor;
      const duration = Math.max(1, end.getTime() - start.getTime());
      return {
        label: "Custom range",
        start,
        end,
        previousStart: new Date(start.getTime() - duration),
        previousEnd: new Date(start.getTime() - 1),
      };
    }

    if (period === "DAY") {
      const start = startOfDay(anchor);
      const end = addDays(start, 1);
      return {
        label: "Latest data day",
        start,
        end,
        previousStart: addDays(start, -1),
        previousEnd: start,
      };
    }

    if (period === "WEEK") {
      const start = startOfWeek(anchor);
      const end = addDays(start, 7);
      return {
        label: "Latest data week",
        start,
        end,
        previousStart: addDays(start, -7),
        previousEnd: start,
      };
    }

    if (period === "YEAR") {
      const start = startOfYear(anchor);
      const end = new Date(anchor.getFullYear() + 1, 0, 1);
      return {
        label: "Latest data year",
        start,
        end,
        previousStart: new Date(anchor.getFullYear() - 1, 0, 1),
        previousEnd: start,
      };
    }

    const start = startOfMonth(anchor);
    const end = new Date(anchor.getFullYear(), anchor.getMonth() + 1, 1);
    return {
      label: "Latest data month",
      start,
      end,
      previousStart: new Date(anchor.getFullYear(), anchor.getMonth() - 1, 1),
      previousEnd: start,
    };
  }, [period, customStart, customEnd, dateBounds.max]);

  const calculateMetrics = (rows) => {
    const totalTransactions = rows.length;
    const successfulTransactions = rows.filter(
      (t) => String(t.status).toUpperCase() === "SUCCESS"
    ).length;
    const failedTransactions = rows.filter(
      (t) => String(t.status).toUpperCase() === "FAILED"
    ).length;
    const totalTransactionValue = rows.reduce(
      (sum, t) => sum + Number(t.amount || 0),
      0
    );
    const revenueAtRisk = rows
      .filter((t) => String(t.status).toUpperCase() === "FAILED")
      .reduce((sum, t) => sum + Number(t.amount || 0), 0);
    const realizedRevenue = rows
      .filter((t) => String(t.status).toUpperCase() === "SUCCESS")
      .reduce((sum, t) => sum + Number(t.amount || 0), 0);

    return {
      totalTransactions,
      successfulTransactions,
      failedTransactions,
      totalTransactionValue,
      revenueAtRisk,
      realizedRevenue,
      successRate:
        totalTransactions ? (successfulTransactions / totalTransactions) * 100 : 0,
      failureRate:
        totalTransactions ? (failedTransactions / totalTransactions) * 100 : 0,
    };
  };

  const currentRows = useMemo(() => {
    if (!periodInfo.start || !periodInfo.end) return [];
    return transactions.filter((t) => {
      const d = new Date(t.createdAt);
      return !Number.isNaN(d.getTime()) && d >= periodInfo.start && d < periodInfo.end;
    });
  }, [transactions, periodInfo]);

  const previousRows = useMemo(() => {
    if (!periodInfo.previousStart || !periodInfo.previousEnd) return [];
    return transactions.filter((t) => {
      const d = new Date(t.createdAt);
      return !Number.isNaN(d.getTime()) && d >= periodInfo.previousStart && d < periodInfo.previousEnd;
    });
  }, [transactions, periodInfo]);

  const current = useMemo(() => calculateMetrics(currentRows), [currentRows]);
  const previous = useMemo(() => calculateMetrics(previousRows), [previousRows]);

  const comparison = (value, previousValue) => {
    if (previousValue === 0) {
      return value === 0 ? { text: "0.0%", positive: true, absolute: 0 } : { text: "New", positive: true, absolute: value };
    }
    const pct = ((value - previousValue) / Math.abs(previousValue)) * 100;
    return {
      text: `${pct >= 0 ? "+" : ""}${pct.toFixed(1)}%`,
      positive: pct >= 0,
      absolute: value - previousValue,
    };
  };

  const valueComparison = comparison(
    current.totalTransactionValue,
    previous.totalTransactionValue
  );
  const revenueComparison = comparison(current.realizedRevenue, previous.realizedRevenue);
  const riskComparison = comparison(current.revenueAtRisk, previous.revenueAtRisk);
  const transactionComparison = comparison(current.totalTransactions, previous.totalTransactions);

  const paymentPerformance = useMemo(() => {
    const groups = new Map();
    currentRows.forEach((transaction) => {
      const method = transaction.paymentMethod || "UNKNOWN";
      const item = groups.get(method) || {
        method,
        transactions: 0,
        successful: 0,
        failed: 0,
        totalValue: 0,
        failedValue: 0,
      };
      const amount = Number(transaction.amount || 0);
      item.transactions += 1;
      item.totalValue += amount;
      if (String(transaction.status).toUpperCase() === "SUCCESS") item.successful += 1;
      if (String(transaction.status).toUpperCase() === "FAILED") {
        item.failed += 1;
        item.failedValue += amount;
      }
      groups.set(method, item);
    });
    return [...groups.values()]
      .map((item) => ({
        ...item,
        successRate: item.transactions ? (item.successful / item.transactions) * 100 : 0,
        failureRate: item.transactions ? (item.failed / item.transactions) * 100 : 0,
      }))
      .sort((a, b) => b.totalValue - a.totalValue);
  }, [currentRows]);

  const failureReasons = useMemo(() => {
    const groups = new Map();
    currentRows
      .filter((t) => String(t.status).toUpperCase() === "FAILED")
      .forEach((t) => {
        const reason = t.failureReason || "UNKNOWN";
        const item = groups.get(reason) || { reason, count: 0, lostValue: 0 };
        item.count += 1;
        item.lostValue += Number(t.amount || 0);
        groups.set(reason, item);
      });
    return [...groups.values()].sort((a, b) => b.lostValue - a.lostValue);
  }, [currentRows]);

  const peakHour = useMemo(() => {
    const hours = Array.from({ length: 24 }, (_, hour) => ({ hour, value: 0, count: 0 }));
    currentRows.forEach((t) => {
      const d = new Date(t.createdAt);
      if (Number.isNaN(d.getTime())) return;
      hours[d.getHours()].value += Number(t.amount || 0);
      hours[d.getHours()].count += 1;
    });
    return hours.sort((a, b) => b.value - a.value)[0];
  }, [currentRows]);

  const formatDate = (d) =>
    d instanceof Date && !Number.isNaN(d.getTime())
      ? d.toLocaleDateString("en-IN", { day: "2-digit", month: "short", year: "numeric" })
      : "—";

  return (
    <section className="module-page">
      <div className="module-page-header">
        <div className="module-page-title">
          <div className="module-icon"><Landmark size={22} strokeWidth={1.8} /></div>
          <div>
            <div className="eyebrow">FINANCIAL CONTROL</div>
            <h2>Finance Control</h2>
            <p>Revenue analytics with period comparison, payment performance and financial exposure.</p>
          </div>
        </div>
        <div className="module-status"><span className="live-indicator" /> Backend connected</div>
      </div>

      {loading ? (
        <div className="module-empty-state"><RefreshCw size={30} /><h3>Loading financial analytics...</h3><p>Fetching live revenue data from the backend.</p></div>
      ) : error ? (
        <div className="module-empty-state"><AlertTriangle size={30} /><h3>Finance analytics unavailable</h3><p>{error}</p><button className="primary-button" onClick={onRefresh}>Retry</button></div>
      ) : (
        <>
          <section className="panel">
            <PanelHeader
              title="Revenue analytics period"
              subtitle={`${periodInfo.label}: ${formatDate(periodInfo.start)} – ${formatDate(new Date(periodInfo.end.getTime() - 1))}`}
              right={<button className="primary-button" onClick={onRefresh}><RefreshCw size={14} /> Refresh</button>}
            />
            <div style={{display:"flex", gap:8, flexWrap:"wrap", alignItems:"center"}}>
              {[['DAY','Today'],['WEEK','Week'],['MONTH','Month'],['YEAR','Year'],['CUSTOM','Custom']].map(([key,label]) => (
                <button key={key} className={`primary-button ${period === key ? "active" : ""}`} onClick={() => setPeriod(key)}>{label}</button>
              ))}
              {period === "CUSTOM" && (
                <>
                  <input type="date" value={customStart} onChange={(e) => setCustomStart(e.target.value)} />
                  <input type="date" value={customEnd} onChange={(e) => setCustomEnd(e.target.value)} />
                </>
              )}
            </div>
            <div className="analysis-empty" style={{marginTop:12}}>
              Comparison baseline: {formatDate(periodInfo.previousStart)} – {formatDate(new Date(periodInfo.previousEnd.getTime() - 1))}. For Today/Week/Month/Year, the period is anchored to the latest transaction date so the existing July–August dataset does not produce a misleading empty September result.
            </div>
          </section>

          <section className="kpi-grid">
            <MetricCard label="Transaction Value" value={formatCompactCurrency(current.totalTransactionValue)} note={`${transactionComparison.text} vs previous period`} positive={valueComparison.positive} />
            <MetricCard label="Realized Revenue" value={formatCompactCurrency(current.realizedRevenue)} note={`${revenueComparison.text} vs previous period`} positive={revenueComparison.positive} />
            <MetricCard label="Revenue at Risk" value={formatCompactCurrency(current.revenueAtRisk)} note={`${riskComparison.text} vs previous period`} positive={riskComparison.positive} />
            <MetricCard label="Transactions" value={formatNumber(current.totalTransactions)} note={`${transactionComparison.text} vs previous period`} positive={transactionComparison.positive} />
            <MetricCard label="Success Rate" value={`${current.successRate.toFixed(1)}%`} note={`${current.successfulTransactions} successful`} positive />
            <MetricCard label="Failure Rate" value={`${current.failureRate.toFixed(1)}%`} note={`${current.failedTransactions} failed`} />
          </section>

          <section className="dashboard-grid">
            <div className="panel">
              <PanelHeader title="Current vs previous" subtitle="Absolute financial change for the selected period" />
              <div className="module-card-grid">
                <div className="module-card"><div className="module-card-icon"><ArrowUpRight size={18}/></div><div><h3>Transaction value change</h3><p>{formatCurrency(valueComparison.absolute)} ({valueComparison.text})</p></div></div>
                <div className="module-card"><div className="module-card-icon"><ArrowUpRight size={18}/></div><div><h3>Realized revenue change</h3><p>{formatCurrency(revenueComparison.absolute)} ({revenueComparison.text})</p></div></div>
                <div className="module-card"><div className="module-card-icon"><ArrowDownRight size={18}/></div><div><h3>Revenue-at-risk change</h3><p>{formatCurrency(riskComparison.absolute)} ({riskComparison.text})</p></div></div>
              </div>
            </div>
            <div className="panel">
              <PanelHeader title="Period snapshot" subtitle="Current and previous transaction performance" />
              <div className="risk-detail-grid">
                <RiskDetail label="Current value" value={formatCurrency(current.totalTransactionValue)} />
                <RiskDetail label="Previous value" value={formatCurrency(previous.totalTransactionValue)} />
                <RiskDetail label="Current revenue" value={formatCurrency(current.realizedRevenue)} />
                <RiskDetail label="Previous revenue" value={formatCurrency(previous.realizedRevenue)} />
                <RiskDetail label="Current failures" value={formatNumber(current.failedTransactions)} />
                <RiskDetail label="Previous failures" value={formatNumber(previous.failedTransactions)} />
              </div>
            </div>
          </section>

          <section className="panel">
            <PanelHeader title="Payment method performance" subtitle="Current selected period" />
            <div className="table-wrapper">
              <table className="transaction-table"><thead><tr><th>Method</th><th>Transactions</th><th>Value</th><th>Success</th><th>Failure</th><th>Failed Value</th></tr></thead>
                <tbody>{paymentPerformance.length ? paymentPerformance.map((item) => <tr key={item.method}><td><strong>{item.method}</strong></td><td>{formatNumber(item.transactions)}</td><td>{formatCurrency(item.totalValue)}</td><td>{item.successRate.toFixed(1)}%</td><td>{item.failureRate.toFixed(1)}%</td><td>{formatCurrency(item.failedValue)}</td></tr>) : <tr><td colSpan="6">No transactions in this period.</td></tr>}</tbody>
              </table>
            </div>
          </section>

          <section className="dashboard-grid">
            <div className="panel">
              <PanelHeader title="Failure reasons" subtitle="Revenue lost by failure reason" />
              <div className="operations-list">{failureReasons.length ? failureReasons.slice(0, 8).map((item) => <div className="operation-row" key={item.reason}><div className="severity-marker danger"/><div className="operation-main"><strong>{item.reason.replaceAll("_", " ")}</strong><span>{formatNumber(item.count)} failed transactions</span></div><strong className="operation-amount">{formatCurrency(item.lostValue)}</strong></div>) : <div className="analysis-empty">No failures in this period.</div>}</div>
            </div>
            <div className="panel">
              <PanelHeader title="Peak sales hour" subtitle="Highest transaction value by hour" />
              {peakHour && currentRows.length ? <div className="module-empty-state"><Activity size={30}/><h3>{String(peakHour.hour).padStart(2,"0")}:00 – {String((peakHour.hour + 1) % 24).padStart(2,"0")}:00</h3><p>{formatCurrency(peakHour.value)} transaction value across {formatNumber(peakHour.count)} transactions.</p></div> : <div className="analysis-empty">No timestamped transactions in this period.</div>}
            </div>
          </section>
        </>
      )}
    </section>
  );
}

function RevenueRecoveryPage({
  opportunity,
  loading,
  error,
  transactions,
  decision,
  decisionLoading,
  decisionError,
  execution,
  executing,
  executionError,
  onSelectTransaction,
  onExecuteRecovery,
}) {
  const failedTransactions = useMemo(
    () =>
      transactions
        .filter(
          (transaction) =>
            String(transaction.status).toUpperCase() === "FAILED"
        )
        .sort(
          (a, b) =>
            Number(b.amount || 0) - Number(a.amount || 0)
        ),
    [transactions]
  );

  const canExecute =
    decision &&
    (decision.recommendedAction === "RETRY" ||
      decision.recommendedAction === "RETRY_WITH_ALTERNATIVE") &&
    !execution?.recoverySuccessful;

  const actionLabel =
    decision?.recommendedAction === "RETRY"
      ? "Execute Retry Simulation"
      : "Execute Alternative Route Simulation";

  return (
    <section className="module-page">
      <div className="module-page-header">
        <div className="module-page-title">
          <div className="module-icon">
            <RefreshCw size={22} strokeWidth={1.8} />
          </div>
          <div>
            <div className="eyebrow">MERCHANT OPERATIONS</div>
            <h2>Revenue Recovery</h2>
            <p>
              Prioritize failed payments using recovery probability,
              expected recovery value and backend decision intelligence.
            </p>
          </div>
        </div>
        <div className="module-status">
          <span className="live-indicator" />
          Recovery engine connected
        </div>
      </div>

      {loading ? (
        <div className="analysis-empty">Loading recovery analytics...</div>
      ) : error ? (
        <div className="analysis-error">
          <AlertTriangle size={17} />
          <span>{error}</span>
        </div>
      ) : (
        <>
          <section className="kpi-grid">
            <RecoveryMetric
              label="Failed Transactions"
              value={formatNumber(opportunity?.failedTransactions)}
              note="currently failed"
              tone="danger"
            />
            <RecoveryMetric
              label="Revenue At Risk"
              value={formatCompactCurrency(opportunity?.revenueAtRisk)}
              note="failed transaction value"
              tone="danger"
            />
            <RecoveryMetric
              label="Expected Recoverable"
              value={formatCompactCurrency(
                opportunity?.expectedRecoverableValue
              )}
              note="weighted recovery value"
            />
            <RecoveryMetric
              label="Opportunity Rate"
              value={`${Number(
                opportunity?.recoveryOpportunityRate || 0
              ).toFixed(2)}%`}
              note="expected recoverable / risk"
            />
          </section>

          <section className="panel">
            <PanelHeader
              title="Recovery Queue"
              subtitle="Highest-value failed transactions requiring recovery analysis"
              right={
                <span className="operations-count">
                  {formatNumber(failedTransactions.length)} failed
                </span>
              }
            />

            <div className="operations-list">
              {failedTransactions.slice(0, 15).map((transaction) => (
                <button
                  className="operation-row"
                  key={transaction.transactionId}
                  onClick={() => onSelectTransaction(transaction.transactionId)}
                >
                  <div className="severity-marker danger" />

                  <div className="operation-main">
                    <strong>{transaction.transactionId}</strong>
                    <span>
                      {transaction.failureReason || "UNKNOWN"}
                      <span className="dot-separator"> · </span>
                      {transaction.paymentMethod || "N/A"}
                      <span className="dot-separator"> · </span>
                      Retry count: {transaction.retryCount ?? 0}
                    </span>
                  </div>

                  <strong className="operation-amount">
                    {formatCurrency(transaction.amount)}
                  </strong>

                  <span className="status-badge danger">FAILED</span>
                </button>
              ))}

              {failedTransactions.length === 0 && (
                <div className="analysis-empty">
                  No failed transactions are currently available for recovery.
                </div>
              )}
            </div>

            {failedTransactions.length > 15 && (
              <div className="table-footer">
                Showing the 15 highest-value failed transactions. Use the
                transaction search or existing transaction intelligence for the
                complete dataset.
              </div>
            )}
          </section>

          <section className="panel">
            <PanelHeader
              title="Recovery Decision"
              subtitle={
                decision
                  ? `Backend decision for ${decision.transactionId}`
                  : "Select a failed transaction from the recovery queue"
              }
              right={
                decision && (
                  <span className="period-label">
                    {decision.decisionConfidence || "N/A"} confidence
                  </span>
                )
              }
            />

            {decisionLoading ? (
              <div className="analysis-empty">
                Calculating recovery decision...
              </div>
            ) : decisionError ? (
              <div className="analysis-error">
                <AlertTriangle size={17} />
                <span>{decisionError}</span>
              </div>
            ) : !decision ? (
              <div className="analysis-empty">
                Select a failed transaction above to calculate its recovery
                probability and recommended action.
              </div>
            ) : (
              <div className="risk-analysis-content">
                <div className="risk-score-card">
                  <span>Recovery score</span>
                  <strong>
                    {Math.round(
                      Number(decision.recoveryProbability || 0) * 100
                    )}
                  </strong>
                  <span
                    className={`status-badge ${String(
                      decision.riskLevel || "unknown"
                    ).toLowerCase()}`}
                  >
                    {decision.riskLevel}
                  </span>
                </div>

                <div className="risk-detail-grid">
                  <RiskDetail
                    label="Transaction"
                    value={decision.transactionId}
                  />
                  <RiskDetail
                    label="Amount"
                    value={formatCurrency(decision.amount)}
                  />
                  <RiskDetail
                    label="Failure reason"
                    value={decision.failureReason || "N/A"}
                  />
                  <RiskDetail
                    label="Retry count"
                    value={decision.retryCount ?? 0}
                  />
                  <RiskDetail
                    label="Recovery probability"
                    value={`${(
                      Number(decision.recoveryProbability || 0) * 100
                    ).toFixed(0)}%`}
                  />
                  <RiskDetail
                    label="Expected recovery"
                    value={formatCurrency(decision.expectedRecoveryValue)}
                  />
                  <RiskDetail
                    label="Customer success rate"
                    value={`${Number(
                      decision.customerSuccessRate || 0
                    ).toFixed(1)}%`}
                  />
                  <RiskDetail
                    label="Payment method success"
                    value={`${Number(
                      decision.paymentMethodSuccessRate || 0
                    ).toFixed(1)}%`}
                  />
                  <RiskDetail
                    label="Merchant failure rate"
                    value={`${Number(
                      decision.merchantFailureRate || 0
                    ).toFixed(1)}%`}
                  />
                </div>

                <div className="business-detail-grid">
                  <BusinessDetail
                    label="Recommended action"
                    value={decision.recommendedAction}
                  />
                  <BusinessDetail
                    label="Decision confidence"
                    value={decision.decisionConfidence || "N/A"}
                  />
                </div>

                <div className="module-empty-state">
                  <h3>{decision.recommendedAction}</h3>
                  <p>{decision.decisionReason}</p>

                  <RecoverySignalList
                    title="Recovery signals"
                    signals={decision.recoverySignals}
                  />

                  <RecoverySignalList
                    title="Risk signals"
                    signals={decision.riskSignals}
                  />

                  {canExecute ? (
                    <button
                      className="primary-button"
                      onClick={onExecuteRecovery}
                      disabled={executing}
                    >
                      {executing ? "Executing..." : actionLabel}
                    </button>
                  ) : decision.recommendedAction === "CUSTOMER_REMINDER" ? (
                    <div className="analysis-empty">
                      Customer reminder is recommended. Automatic recovery is
                      intentionally not executed for this decision.
                    </div>
                  ) : decision.recommendedAction === "DO_NOT_RETRY" ? (
                    <div className="analysis-empty">
                      Recovery execution is blocked because recovery potential
                      is too low.
                    </div>
                  ) : null}
                </div>
              </div>
            )}
          </section>

          {execution && (
            <section className="panel">
              <PanelHeader
                title="Recovery Execution"
                subtitle="Result returned by the backend recovery simulator"
                right={
                  <span
                    className={`status-badge ${
                      execution.recoverySuccessful ? "success" : "danger"
                    }`}
                  >
                    {execution.recoverySuccessful ? "SUCCESS" : "FAILED"}
                  </span>
                }
              />

              <div className="risk-detail-grid">
                <RiskDetail
                  label="Transaction"
                  value={execution.transactionId}
                />
                <RiskDetail
                  label="Recovery score"
                  value={Number(execution.recoveryScore || 0).toFixed(2)}
                />
                <RiskDetail
                  label="Final status"
                  value={execution.status}
                />
                <RiskDetail
                  label="Retry count"
                  value={execution.retryCount ?? 0}
                />
                <RiskDetail
                  label="Recovered revenue"
                  value={formatCurrency(execution.recoveredRevenue)}
                />
              </div>

              <div className="module-empty-state">
                <h3>
                  {execution.recoverySuccessful
                    ? "Recovery simulation succeeded"
                    : execution.recoveryAttempted
                      ? "Recovery simulation failed"
                      : "Recovery was not executed"}
                </h3>
                <p>{execution.message}</p>
                <p>
                  This is a backend recovery simulation, not a real payment
                  gateway charge.
                </p>
              </div>
            </section>
          )}

          {executionError && (
            <section className="panel">
              <div className="analysis-error">
                <AlertTriangle size={17} />
                <span>{executionError}</span>
              </div>
            </section>
          )}
        </>
      )}
    </section>
  );
}


function FunctionalModulePage({
  title,
  transactions,
  onSelectTransaction,
  onBack,
  darkMode,
  setDarkMode,
  notificationPrefs,
  toggleNotificationPrefs,
  aiPrefs,
  toggleAiPrefs,
}) {
  const rows = Array.isArray(transactions) ? transactions : [];
  const failed = rows.filter(
    (t) => String(t.status).toUpperCase() === "FAILED"
  );
  const successful = rows.filter(
    (t) => String(t.status).toUpperCase() === "SUCCESS"
  );

  const amount = (items) =>
    items.reduce((sum, t) => sum + Number(t.amount || 0), 0);

  const totalValue = amount(rows);
  const failedValue = amount(failed);
  const successValue = amount(successful);

  const methodStats = useMemo(() => {
    const map = new Map();
    rows.forEach((t) => {
      const method = t.paymentMethod || "UNKNOWN";
      const item = map.get(method) || {
        method,
        count: 0,
        success: 0,
        failed: 0,
        value: 0,
        failedValue: 0,
      };
      item.count += 1;
      item.value += Number(t.amount || 0);
      if (String(t.status).toUpperCase() === "FAILED") {
        item.failed += 1;
        item.failedValue += Number(t.amount || 0);
      } else if (String(t.status).toUpperCase() === "SUCCESS") {
        item.success += 1;
      }
      map.set(method, item);
    });

    return [...map.values()]
      .map((item) => ({
        ...item,
        successRate: item.count ? (item.success / item.count) * 100 : 0,
        failureRate: item.count ? (item.failed / item.count) * 100 : 0,
      }))
      .sort((a, b) => b.value - a.value);
  }, [rows]);

  const customerStats = useMemo(() => {
    const map = new Map();
    rows.forEach((t) => {
      const customer = t.customerId || "UNKNOWN";
      const item = map.get(customer) || {
        customer,
        count: 0,
        failed: 0,
        value: 0,
      };
      item.count += 1;
      item.value += Number(t.amount || 0);
      if (String(t.status).toUpperCase() === "FAILED") item.failed += 1;
      map.set(customer, item);
    });
    return [...map.values()]
      .map((item) => ({
        ...item,
        failureRate: item.count ? (item.failed / item.count) * 100 : 0,
      }))
      .sort((a, b) => b.value - a.value);
  }, [rows]);

  const failureReasons = useMemo(() => {
    const map = new Map();
    failed.forEach((t) => {
      const reason = t.failureReason || "UNKNOWN";
      const item = map.get(reason) || { reason, count: 0, value: 0 };
      item.count += 1;
      item.value += Number(t.amount || 0);
      map.set(reason, item);
    });
    return [...map.values()].sort((a, b) => b.value - a.value);
  }, [failed]);

  const riskCandidates = useMemo(
    () =>
      [...failed]
        .sort(
          (a, b) =>
            Number(b.amount || 0) - Number(a.amount || 0)
        )
        .slice(0, 10),
    [failed]
  );

  const titleData = {
    Transactions: {
      eyebrow: "TRANSACTION ANALYTICS",
      subtitle: "Live transaction records loaded from the merchant backend.",
    },
    Customers: {
      eyebrow: "CUSTOMER ANALYTICS",
      subtitle: "Customer payment activity and failure behaviour.",
    },
    "Payment Methods": {
      eyebrow: "PAYMENT ANALYTICS",
      subtitle: "Payment-method usage, success and financial exposure.",
    },
    Alerts: {
      eyebrow: "OPERATIONAL ALERTS",
      subtitle: "Prioritised events generated from the current transaction dataset.",
    },
    "Recovery Actions": {
      eyebrow: "RECOVERY OPERATIONS",
      subtitle: "Failed payments ranked for recovery review and decisioning.",
    },
    Reconciliation: {
      eyebrow: "FINANCIAL CONTROL",
      subtitle: "Transaction-level consistency checks across payment records.",
    },
    "AI Decisions": {
      eyebrow: "AI DECISION CENTER",
      subtitle: "Open transaction intelligence and recovery decisions.",
    },
    Settings: {
      eyebrow: "SYSTEM",
      subtitle: "Current workspace and backend configuration.",
    },
  }[title] || {
    eyebrow: "MERCHANT OPERATIONS",
    subtitle: getModuleDescription(title),
  };

  const renderTransactionRows = (items, limit = 12) => (
    <div className="table-wrapper">
      <table className="transaction-table">
        <thead>
          <tr>
            <th>Transaction</th>
            <th>Amount</th>
            <th>Merchant</th>
            <th>Method</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
          {items.slice(0, limit).map((t) => (
            <tr
              key={t.transactionId}
              onClick={() => onSelectTransaction?.(t.transactionId)}
            >
              <td><code>{t.transactionId}</code></td>
              <td>{formatCurrency(t.amount)}</td>
              <td>{t.merchantId || "—"}</td>
              <td>{t.paymentMethod || "—"}</td>
              <td>
                <span
                  className={`status-badge ${String(
                    t.status || "unknown"
                  ).toLowerCase()}`}
                >
                  {t.status || "UNKNOWN"}
                </span>
              </td>
            </tr>
          ))}
          {!items.length && (
            <tr>
              <td colSpan="5">No matching records.</td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );

  return (
    <section className="module-page">
      <div className="module-page-header">
        <div className="module-page-title">
          <div className="module-icon">
            {title === "Customers" ? (
              <Users size={22} />
            ) : title === "Payment Methods" ? (
              <CreditCard size={22} />
            ) : title === "Alerts" ? (
              <Bell size={22} />
            ) : title === "Reconciliation" ? (
              <GitCompare size={22} />
            ) : title === "AI Decisions" ? (
              <BrainCircuit size={22} />
            ) : (
              <Activity size={22} />
            )}
          </div>
          <div>
            <div className="eyebrow">{titleData.eyebrow}</div>
            <h2>{title}</h2>
            <p>{titleData.subtitle}</p>
          </div>
        </div>
        <div className="module-status">
          <span className="live-indicator" />
          {rows.length ? "Live backend data" : "Waiting for backend data"}
        </div>
      </div>

      {title === "Transactions" && (
        <>
          <section className="kpi-grid">
            <MetricCard label="Transactions" value={formatNumber(rows.length)} note="loaded records" positive />
            <MetricCard label="Transaction Value" value={formatCompactCurrency(totalValue)} note="all payment activity" positive />
            <MetricCard label="Successful" value={formatNumber(successful.length)} note={`${rows.length ? ((successful.length / rows.length) * 100).toFixed(1) : "0.0"}% success rate`} positive />
            <MetricCard label="Failed" value={formatNumber(failed.length)} note={`${rows.length ? ((failed.length / rows.length) * 100).toFixed(1) : "0.0"}% failure rate`} />
          </section>
          <section className="panel">
            <PanelHeader title="Transaction dataset" subtitle="Click a row to open transaction intelligence." />
            {renderTransactionRows(rows)}
          </section>
        </>
      )}

      {title === "Customers" && (
        <>
          <section className="kpi-grid">
            <MetricCard label="Customers" value={formatNumber(customerStats.length)} note="unique customer IDs" positive />
            <MetricCard label="Customer Value" value={formatCompactCurrency(totalValue)} note="transaction value" positive />
            <MetricCard label="Customers With Failures" value={formatNumber(customerStats.filter((c) => c.failed > 0).length)} note="requires review" />
            <MetricCard label="Highest Customer Value" value={customerStats[0] ? formatCurrency(customerStats[0].value) : "₹0.00"} note={customerStats[0]?.customer || "No data"} positive />
          </section>
          <section className="panel">
            <PanelHeader title="Customer activity" subtitle="Highest-value customers in the current dataset." />
            <div className="table-wrapper">
              <table className="transaction-table">
                <thead><tr><th>Customer</th><th>Transactions</th><th>Value</th><th>Failures</th><th>Failure Rate</th></tr></thead>
                <tbody>
                  {customerStats.slice(0, 20).map((c) => (
                    <tr key={c.customer}>
                      <td><code>{c.customer}</code></td>
                      <td>{formatNumber(c.count)}</td>
                      <td>{formatCurrency(c.value)}</td>
                      <td>{formatNumber(c.failed)}</td>
                      <td>{c.failureRate.toFixed(1)}%</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </section>
        </>
      )}

      {title === "Payment Methods" && (
        <>
          <section className="kpi-grid">
            <MetricCard label="Payment Methods" value={formatNumber(methodStats.length)} note="active methods" positive />
            <MetricCard label="Top Method" value={methodStats[0]?.method || "—"} note={methodStats[0] ? `${formatNumber(methodStats[0].count)} transactions` : "No data"} positive />
            <MetricCard label="Highest Failure Rate" value={methodStats.length ? `${Math.max(...methodStats.map((m) => m.failureRate)).toFixed(1)}%` : "0.0%"} note="across payment methods" />
            <MetricCard label="Failed Value" value={formatCompactCurrency(failedValue)} note="payment value affected" />
          </section>
          <section className="panel">
            <PanelHeader title="Payment method performance" subtitle="Usage, success and financial exposure." />
            <div className="table-wrapper">
              <table className="transaction-table">
                <thead><tr><th>Method</th><th>Transactions</th><th>Value</th><th>Success Rate</th><th>Failure Rate</th><th>Failed Value</th></tr></thead>
                <tbody>
                  {methodStats.map((m) => (
                    <tr key={m.method}>
                      <td><strong>{m.method}</strong></td><td>{formatNumber(m.count)}</td><td>{formatCurrency(m.value)}</td><td>{m.successRate.toFixed(1)}%</td><td>{m.failureRate.toFixed(1)}%</td><td>{formatCurrency(m.failedValue)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </section>
        </>
      )}

      {title === "Alerts" && (
        <>
          <section className="kpi-grid">
            <MetricCard label="Open Alerts" value={formatNumber(failed.length)} note="failed payments" />
            <MetricCard label="High-Value Alerts" value={formatNumber(failed.filter((t) => Number(t.amount || 0) >= 20000).length)} note="₹20K+ failed payments" />
            <MetricCard label="Retry Alerts" value={formatNumber(failed.filter((t) => Number(t.retryCount || 0) >= 2).length)} note="multiple retries" />
            <MetricCard label="Revenue Exposure" value={formatCompactCurrency(failedValue)} note="failed transaction value" />
          </section>
          <section className="panel">
            <PanelHeader title="Priority alerts" subtitle="Highest-value failed transactions requiring attention." />
            {renderTransactionRows(failed)}
          </section>
        </>
      )}

      {title === "Recovery Actions" && (
        <>
          <section className="kpi-grid">
            <MetricCard label="Recovery Queue" value={formatNumber(failed.length)} note="failed transactions" />
            <MetricCard label="Recoverable Exposure" value={formatCompactCurrency(failedValue)} note="revenue currently at risk" />
            <MetricCard label="High Value" value={formatNumber(failed.filter((t) => Number(t.amount || 0) >= 20000).length)} note="priority opportunities" />
            <MetricCard label="Retried" value={formatNumber(failed.filter((t) => Number(t.retryCount || 0) > 0).length)} note="already attempted" />
          </section>
          <section className="panel">
            <PanelHeader title="Recovery action queue" subtitle="Select a failed transaction to open its recovery decision." />
            {renderTransactionRows(riskCandidates)}
          </section>
        </>
      )}

      {title === "Reconciliation" && (
        <>
          <section className="kpi-grid">
            <MetricCard label="Records Checked" value={formatNumber(rows.length)} note="transaction dataset" positive />
            <MetricCard label="Successful Value" value={formatCompactCurrency(successValue)} note="completed payments" positive />
            <MetricCard label="Failed Value" value={formatCompactCurrency(failedValue)} note="requires reconciliation" />
            <MetricCard label="Unresolved" value={formatNumber(failed.length)} note="failed records" />
          </section>
          <section className="panel">
            <PanelHeader title="Payment differences" subtitle="Failed records are surfaced as financial exceptions for review." />
            {renderTransactionRows(failed)}
          </section>
        </>
      )}

      {title === "AI Decisions" && (
        <>
          <section className="kpi-grid">
            <MetricCard label="Decision Queue" value={formatNumber(failed.length)} note="failed transactions" />
            <MetricCard label="High-Value Queue" value={formatNumber(riskCandidates.filter((t) => Number(t.amount || 0) >= 20000).length)} note="priority decisions" />
            <MetricCard label="Revenue at Risk" value={formatCompactCurrency(failedValue)} note="decision exposure" />
            <MetricCard label="AI Engine" value="READY" note="transaction decisioning available" positive />
          </section>
          <section className="panel">
            <PanelHeader title="AI decision queue" subtitle="Click a transaction to calculate its recovery decision." />
            {renderTransactionRows(riskCandidates)}
          </section>
        </>
      )}

      {title === "Settings" && (
        <>
          <section className="settings-grid">
            <div className="panel settings-card">
              <PanelHeader title="Business" subtitle="Basic merchant workspace information." />
              <div className="settings-fields">
                <label><span>Business name</span><input defaultValue="Payment Intelligence Merchant" /></label>
                <label><span>Business category</span><select defaultValue="Retail"><option>Retail</option><option>Food & Beverage</option><option>Services</option><option>Other</option></select></label>
                <label><span>Currency</span><select defaultValue="INR"><option>INR — Indian Rupee</option><option>USD — US Dollar</option></select></label>
                <label><span>Timezone</span><select defaultValue="Asia/Kolkata"><option>Asia/Kolkata (IST)</option><option>UTC</option></select></label>
              </div>
            </div>

            <div className="panel settings-card">
              <PanelHeader title="Notifications" subtitle="Choose which merchant alerts you receive." />
              <div className="settings-option">
                <div><strong>Payment & risk alerts</strong><span>Failure, high-risk and recovery opportunity notifications.</span></div>
                <button type="button" className={`theme-toggle ${notificationPrefs ? "is-on" : ""}`} onClick={toggleNotificationPrefs} aria-label="Toggle notifications" aria-pressed={notificationPrefs}><span className="theme-toggle-knob" /></button>
              </div>
            </div>

            <div className="panel settings-card">
              <PanelHeader title="AI Preferences" subtitle="Control intelligence recommendations shown in the workspace." />
              <div className="settings-option">
                <div><strong>AI insights & recommendations</strong><span>Business insights, risk explanations and recovery recommendations.</span></div>
                <button type="button" className={`theme-toggle ${aiPrefs ? "is-on" : ""}`} onClick={toggleAiPrefs} aria-label="Toggle AI insights" aria-pressed={aiPrefs}><span className="theme-toggle-knob" /></button>
              </div>
            </div>

            <div className="panel settings-card">
              <PanelHeader title="Appearance" subtitle="Personalize the workspace interface." />
              <div className="settings-option">
                <div className="theme-setting-info">
                  <div className="theme-setting-title">{darkMode ? <Moon size={17} /> : <Sun size={17} />}<h3>Dark mode</h3></div>
                  <p>{darkMode ? "Dark interface is enabled." : "Use a dark interface for the workspace."}</p>
                </div>
                <button type="button" className={`theme-toggle ${darkMode ? "is-on" : ""}`} onClick={() => setDarkMode((value) => !value)} aria-label="Toggle dark mode" aria-pressed={darkMode}><span className="theme-toggle-knob" /></button>
              </div>
            </div>
          </section>

          <section className="panel settings-card">
            <PanelHeader title="Security" subtitle="Basic account security controls." />
            <div className="settings-actions"><button className="secondary-button" type="button">Change password</button><button className="secondary-button" type="button">Log out all devices</button></div>
          </section>

          <section className="panel settings-card danger-zone">
            <PanelHeader title="Help & Support" subtitle="Need help with your merchant workspace?" />
            <div className="support-row"><div><strong>Merchant Support</strong><span>Contact your support helpline for account or payment assistance.</span></div><button className="secondary-button" type="button">Contact Support</button></div>
          </section>
        </>
      )}

      {title === "Alerts" && failureReasons.length > 0 && (
        <section className="panel">
          <PanelHeader title="Failure patterns" subtitle="Failure reasons ranked by affected payment value." />
          <div className="operations-list">
            {failureReasons.slice(0, 8).map((item) => (
              <div className="operation-row" key={item.reason}>
                <div className="severity-marker danger" />
                <div className="operation-main">
                  <strong>{item.reason.replaceAll("_", " ")}</strong>
                  <span>{formatNumber(item.count)} failed transactions</span>
                </div>
                <strong className="operation-amount">{formatCurrency(item.value)}</strong>
              </div>
            ))}
          </div>
        </section>
      )}

      {!["Transactions", "Customers", "Payment Methods", "Alerts", "Recovery Actions", "Reconciliation", "AI Decisions", "Settings"].includes(title) && (
        <ModulePage
          title={title}
          icon={navigation.flatMap((section) => section.items).find((item) => item.name === title)?.icon || LayoutDashboard}
          description={getModuleDescription(title)}
          items={getModuleItems(title)}
          onBack={onBack}
        />
      )}

      <button className="primary-button" onClick={onBack}>Back to Overview</button>
    </section>
  );
}
function RiskIntelligencePage({
  riskAnalytics,
  riskLoading,
  riskError,
  transactions,
  onBack,
  onRefresh,
}) {
  const [selectedId, setSelectedId] = useState("");

  const total = Number(
    riskAnalytics?.totalTransactions || transactions.length || 0
  );

  const highRisk = Number(
    riskAnalytics?.highRiskTransactions || 0
  );

  const mediumRisk = Number(
    riskAnalytics?.mediumRiskTransactions || 0
  );

  const lowRisk = Number(
    riskAnalytics?.lowRiskTransactions || 0
  );

  const riskPercentage = (count) =>
    total > 0
      ? ((count / total) * 100).toFixed(1)
      : "0.0";

  const selectedTransaction =
    transactions.find(
      (transaction) =>
        transaction.transactionId === selectedId
    ) || null;

  return (
    <section className="module-page">

      <div className="module-page-header">

        <div className="module-page-title">

          <div className="module-icon">
            <ShieldCheck
              size={22}
              strokeWidth={1.8}
            />
          </div>

          <div>
            <div className="eyebrow">
              MERCHANT OPERATIONS
            </div>

            <h2>Risk Intelligence</h2>

            <p>
              Monitor transaction-level risk,
              risk signals and merchant exposure.
            </p>
          </div>

        </div>

        <div className="module-status">
          <span className="live-indicator" />
          Live backend data
        </div>

      </div>

      {riskLoading ? (

        <div className="analysis-empty">
          Loading risk intelligence...
        </div>

      ) : riskError ? (

        <div className="analysis-empty">
          {riskError}
        </div>

      ) : (

        <>

          <div className="module-card-grid">

            <div className="module-card">
              <div className="module-card-icon">
                <ShieldCheck size={18} />
              </div>

              <div>
                <h3>Total Analysed</h3>
                <p>
                  {formatNumber(total)} transactions
                </p>
              </div>
            </div>

            <div className="module-card">
              <div className="module-card-icon">
                <CheckCircle2 size={18} />
              </div>

              <div>
                <h3>Low Risk</h3>
                <p>
                  {formatNumber(lowRisk)} (
                  {riskPercentage(lowRisk)}%)
                </p>
              </div>
            </div>

            <div className="module-card">
              <div className="module-card-icon">
                <AlertTriangle size={18} />
              </div>

              <div>
                <h3>Medium Risk</h3>
                <p>
                  {formatNumber(mediumRisk)} (
                  {riskPercentage(mediumRisk)}%)
                </p>
              </div>
            </div>

            <div className="module-card">
              <div className="module-card-icon">
                <ShieldAlert size={18} />
              </div>

              <div>
                <h3>High Risk</h3>
                <p>
                  {formatNumber(highRisk)} (
                  {riskPercentage(highRisk)}%)
                </p>
              </div>
            </div>

          </div>

          <section className="panel">

            <PanelHeader
              title="Risk Distribution"
              subtitle="Backend-generated transaction risk classification"
              right={
                <button
                  className="primary-button"
                  onClick={onRefresh}
                >
                  Refresh
                </button>
              }
            />

            <div className="analysis-grid">

              <div className="analysis-item">
                <span>Low Risk</span>
                <strong>
                  {formatNumber(lowRisk)}
                </strong>
              </div>

              <div className="analysis-item">
                <span>Medium Risk</span>
                <strong>
                  {formatNumber(mediumRisk)}
                </strong>
              </div>

              <div className="analysis-item">
                <span>High Risk</span>
                <strong>
                  {formatNumber(highRisk)}
                </strong>
              </div>

            </div>

          </section>

          <section className="panel">

            <PanelHeader
              title="Transaction Risk Inspector"
              subtitle="Inspect individual transaction risk"
            />

            <select
              value={selectedId}
              onChange={(event) =>
                setSelectedId(event.target.value)
              }
              style={{
                minWidth: "280px",
                padding: "10px 12px",
                borderRadius: "8px",
                border: "1px solid #ddd",
                marginBottom: "20px",
              }}
            >

              <option value="">
                Select a transaction
              </option>

              {transactions.map((transaction) => (

                <option
                  key={transaction.transactionId}
                  value={transaction.transactionId}
                >
                  {transaction.transactionId} —{" "}
                  {transaction.status} —{" "}
                  {formatCurrency(transaction.amount)}
                </option>

              ))}

            </select>

            {selectedTransaction ? (

              <RiskTransactionDetails
                transaction={selectedTransaction}
              />

            ) : (

              <div className="analysis-empty">
                Select a transaction to inspect its risk profile.
              </div>

            )}

          </section>

        </>

      )}

      <button
        className="primary-button"
        onClick={onBack}
      >
        Back to Overview
      </button>

    </section>
  );
}

function ModulePage({ title, icon: Icon, description, items, onBack }) {
  return (
    <section className="module-page">
      <div className="module-page-header">
        <div className="module-page-title">
          <div className="module-icon"><Icon size={22} strokeWidth={1.8} /></div>
          <div>
            <div className="eyebrow">MERCHANT OPERATIONS</div>
            <h2>{title}</h2>
            <p>{description}</p>
          </div>
        </div>
        <div className="module-status"><span className="live-indicator" /> Backend connected</div>
      </div>
      <div className="module-card-grid">
        {items.map((item) => {
          const ItemIcon = item.icon;
          return (
            <div className="module-card" key={item.title}>
              <div className="module-card-icon"><ItemIcon size={18} strokeWidth={1.8} /></div>
              <div><h3>{item.title}</h3><p>{item.description}</p></div>
            </div>
          );
        })}
      </div>
      <div className="module-empty-state">
        <Icon size={30} strokeWidth={1.5} />
        <h3>{title}</h3>
        <p>This module is connected to the merchant intelligence workspace. Detailed controls can be added without changing the existing navigation structure.</p>
        <button className="primary-button" onClick={onBack}>Back to Overview</button>
      </div>
    </section>
  );
}

export default App;
