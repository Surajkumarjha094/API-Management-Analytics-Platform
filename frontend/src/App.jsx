import { useState, useEffect } from "react";
import "./App.css";
import Login from "./pages/Login";
import {
  getRoutes,
  getAnalyticsSummary,
  getAnalyticsEvents,
  getAuditRecords,
  getHeaderRules,
  createHeaderRule,
  deleteHeaderRule,
  createRoute,
  deleteRoute,
  updateRoute,
} from "./services/api";

function App() {
  const getGreeting = () => {
    const hour = new Date().getHours();

    if (hour >= 5 && hour < 12) {
      return "Good morning";
    }

    if (hour >= 12 && hour < 17) {
      return "Good afternoon";
    }

    return "Good evening";
  };

  // baaki tumhara existing code...
  const [isLoggedIn, setIsLoggedIn] = useState(
    !!localStorage.getItem("gatekeeper_token")
  );
  const [showCreateRoute, setShowCreateRoute] = useState(false);
  const [editingRoute, setEditingRoute] = useState(null);

  const [routeForm, setRouteForm] = useState({
    routeId: "",
    name: "",
    pathPattern: "",
    targetBaseUrl: "",
    targetPathPrefix: "",
    requestsPerMinute: 100,
    responseTimeoutMs: 5000,
    idempotencyEnabled: false,
    active: true,
  });

  const [creatingRoute, setCreatingRoute] = useState(false);
  const [routeError, setRouteError] = useState("");
  const handleCreateRoute = async (e) => {
    e.preventDefault();

    setCreatingRoute(true);
    setRouteError("");

    try {
      const routeData = {
        ...routeForm,
        requestsPerMinute: Number(routeForm.requestsPerMinute),
        responseTimeoutMs: Number(routeForm.responseTimeoutMs),
      };

      if (editingRoute) {
        await updateRoute(editingRoute.routeId, routeData);
      } else {
        await createRoute(routeData);
      }

      const updatedRoutes = await getRoutes();
      setRoutes(updatedRoutes);

      setShowCreateRoute(false);
      setEditingRoute(null);

      setRouteForm({
        routeId: "",
        name: "",
        pathPattern: "",
        targetBaseUrl: "",
        targetPathPrefix: "",
        requestsPerMinute: 100,
        responseTimeoutMs: 5000,
        idempotencyEnabled: false,
      });
    } catch (error) {
      setRouteError(
        error.message ||
          (editingRoute ? "Failed to update route" : "Failed to create route")
      );
    } finally {
      setCreatingRoute(false);
    }
  };
  const handleDeleteRoute = async (routeId) => {
    const confirmed = window.confirm(
      `Are you sure you want to delete route "${routeId}"?`
    );

    if (!confirmed) {
      return;
    }

    try {
      await deleteRoute(routeId);

      const updatedRoutes = await getRoutes();
      setRoutes(updatedRoutes);
    } catch (error) {
      alert(error.message || "Failed to delete route");
    }
  };
  const handleEditRoute = (route) => {
    setEditingRoute(route);

    setRouteForm({
      routeId: route.routeId || "",
      name: route.name || "",
      pathPattern: route.pathPattern || "",
      targetBaseUrl: route.targetBaseUrl || "",
      targetPathPrefix: route.targetPathPrefix || "",
      requestsPerMinute: route.requestsPerMinute || 100,
      responseTimeoutMs: route.responseTimeoutMs || 5000,
      idempotencyEnabled: route.idempotencyEnabled || false,
      active: route.active ?? false,
    });

    setShowCreateRoute(true);
    setRouteError("");
  };
  const handleAddHeaderRule = async () => {
    try {
      if (!headerRuleForm.headerName.trim()) {
        alert("Header name is required");
        return;
      }

      if (
        headerRuleForm.action !== "REMOVE" &&
        !headerRuleForm.headerValue.trim()
      ) {
        alert("Header value is required");
        return;
      }

      await createHeaderRule(headerRuleForm.routeId, {
        direction: headerRuleForm.direction,
        action: headerRuleForm.action,
        headerName: headerRuleForm.headerName,
        headerValue: headerRuleForm.headerValue,
      });

      const rules = await getHeaderRules(headerRuleForm.routeId);

      setHeaderRules(rules);

      setShowHeaderRuleForm(false);

      setHeaderRuleForm({
        routeId: "products-api",
        direction: "REQUEST",
        action: "ADD",
        headerName: "",
        headerValue: "",
      });
    } catch (error) {
      alert(error.message || "Failed to add header rule");
    }
  };
  const handleDeleteHeaderRule = async (routeId, ruleId) => {
    try {
      await deleteHeaderRule(routeId, ruleId);

      setHeaderRules((prev) => prev.filter((rule) => rule.id !== ruleId));
    } catch (error) {
      console.error("Failed to delete header rule:", error);
      alert(error.message || "Failed to delete header rule");
    }
  };
  const [activePage, setActivePage] = useState("Dashboard");
  const [routes, setRoutes] = useState([]);
  const [analyticsSummary, setAnalyticsSummary] = useState(null);
  const [analyticsEvents, setAnalyticsEvents] = useState([]);
  const [auditRecords, setAuditRecords] = useState([]);
  const [headerRules, setHeaderRules] = useState([]);
  const [showHeaderRuleForm, setShowHeaderRuleForm] = useState(false);

  const [headerRuleForm, setHeaderRuleForm] = useState({
    routeId: "products-api",
    direction: "REQUEST",
    action: "ADD",
    headerName: "",
    headerValue: "",
  });

  useEffect(() => {
    if (!isLoggedIn) return;

    const loadDashboardData = async () => {
      try {
        const [routesData, summaryData, eventsData, auditData] =
          await Promise.all([
            getRoutes(),
            getAnalyticsSummary(),
            getAnalyticsEvents(),
            getAuditRecords(),
          ]);

        setRoutes(routesData);

        const rules = await getHeaderRules("products-api");
        setHeaderRules(rules);

        setAnalyticsSummary(summaryData);
        setAnalyticsEvents(eventsData);
        setAuditRecords(auditData);
      } catch (error) {
        console.error("Failed to load dashboard data:", error);
      }
    };

    loadDashboardData();
  }, [isLoggedIn]);

  if (!isLoggedIn) {
    return <Login onLogin={() => setIsLoggedIn(true)} />;
  }

  const menuItems = [
    { name: "Dashboard", icon: "▦" },
    { name: "Routes", icon: "⇄" },
    { name: "Header Rules", icon: "≡" },
    { name: "Analytics", icon: "⌁" },
    { name: "Audit Logs", icon: "◷" },
  ];

  /* =========================
     REAL DASHBOARD DATA
     ========================= */

  const totalRoutes = routes.length;

  const activeRoutes = routes.filter((route) => route.active === true).length;

  const totalRequests = analyticsSummary?.totalRequests ?? 0;

  const successfulRequests = analyticsSummary?.successfulRequests ?? 0;

  const failedRequests = analyticsSummary?.failedRequests ?? 0;

  const successPercentage =
    totalRequests > 0
      ? Math.round((successfulRequests / totalRequests) * 100)
      : 0;

  /* =========================
   TRAFFIC DATA
   ========================= */

  const [trafficRange, setTrafficRange] = useState(7);

  const today = new Date();

  const trafficData = Array.from({ length: trafficRange }, (_, index) => {
    const date = new Date(today);

    date.setDate(today.getDate() - (trafficRange - 1 - index));

    const dateKey = date.toISOString().split("T")[0];

    const count = analyticsEvents.filter((event) => {
      if (!event.timestamp) return false;

      const eventDate = new Date(event.timestamp);
      const eventDateKey = eventDate.toISOString().split("T")[0];

      return eventDateKey === dateKey;
    }).length;

    return {
      day:
        trafficRange === 7
          ? date.toLocaleDateString("en-US", {
              weekday: "short",
            })
          : date.toLocaleDateString("en-US", {
              day: "numeric",
              month: "short",
            }),
      count,
    };
  });

  const maxTraffic = Math.max(...trafficData.map((item) => item.count), 1);

  /* =========================
     RECENT ACTIVITY
     ========================= */

  const recentEvents = [...analyticsEvents]
    .sort((a, b) => {
      return new Date(b.timestamp || 0) - new Date(a.timestamp || 0);
    })
    .slice(0, 5);

  const getRouteName = (routeId) => {
    const route = routes.find(
      (item) => item.routeId === routeId || String(item.id) === String(routeId)
    );

    return route?.name || routeId || "Unknown Route";
  };

  const formatTime = (timestamp) => {
    if (!timestamp) return "-";

    const date = new Date(timestamp);

    if (Number.isNaN(date.getTime())) {
      return "-";
    }

    return date.toLocaleTimeString("en-US", {
      hour: "numeric",
      minute: "2-digit",
    });
  };

  return (
    <div className="app-shell">
      {/* Sidebar */}
      <aside className="sidebar">
        <div className="brand">
          <div className="brand-icon">G</div>

          <div>
            <div className="brand-name">Gatekeeper</div>
            <div className="brand-subtitle">API MANAGEMENT</div>
          </div>
        </div>

        <div className="workspace">
          <span className="workspace-dot"></span>

          <div>
            <div className="workspace-title">Suraj Organization</div>

            <div className="workspace-subtitle">Tenant Admin</div>
          </div>

          <span className="workspace-arrow">⌄</span>
        </div>

        <nav className="navigation">
          <div className="nav-label">MAIN MENU</div>

          {menuItems.map((item) => (
            <button
              key={item.name}
              className={`nav-item ${activePage === item.name ? "active" : ""}`}
              onClick={() => setActivePage(item.name)}
            >
              <span className="nav-icon">{item.icon}</span>

              <span>{item.name}</span>
            </button>
          ))}
        </nav>

        <div className="sidebar-bottom">
          <button className="nav-item">
            <span className="nav-icon">⚙</span>
            <span>Settings</span>
          </button>

          <button
            className="nav-item logout"
            onClick={() => {
              localStorage.removeItem("gatekeeper_token");
              localStorage.removeItem("gatekeeper_username");
              setIsLoggedIn(false);
            }}
          >
            <span className="nav-icon">↪</span>
            <span>Logout</span>
          </button>

          <div className="user-card">
            <div className="avatar">SJ</div>

            <div className="user-info">
              <div className="user-name">Suraj Jha</div>

              <div className="user-role">Administrator</div>
            </div>

            <span className="user-more">•••</span>
          </div>
        </div>
      </aside>

      {/* Main Content */}
      <main className="main-content">
        {/* Topbar */}
        <header className="topbar">
          <div>
            <div className="breadcrumb">Workspace / {activePage}</div>

            <h1>{activePage}</h1>
          </div>

          <div className="topbar-actions">
            <button className="icon-button">⌕</button>

            <button className="icon-button notification">
              ♢<span></span>
            </button>

            <div className="top-user">
              <div className="avatar small">SJ</div>

              <div>
                <div className="top-user-name">Suraj Jha</div>

                <div className="top-user-role">Tenant Admin</div>
              </div>

              <span>⌄</span>
            </div>
          </div>
        </header>

        {/* =========================
            DASHBOARD
            ========================= */}

        {activePage === "Dashboard" && (
          <div className="page-content">
            <div className="welcome-row">
              <div>
                <h2>{getGreeting()}, Suraj 👋</h2>

                <p>Here's what's happening with your APIs today.</p>
              </div>

              <button
                className="primary-button"
                onClick={() => {
                  setShowCreateRoute(true);
                  setRouteError("");
                }}
              >
                + Create Route
              </button>
            </div>

            {/* Stats */}
            <section className="stats-grid">
              {/* Total Routes */}
              <div className="stat-card">
                <div className="stat-header">
                  <span className="stat-label">TOTAL ROUTES</span>

                  <span className="stat-icon blue">⇄</span>
                </div>

                <div className="stat-value">{totalRoutes}</div>

                <div className="stat-footer">
                  <span className="status-dot green"></span>
                  {activeRoutes} active route
                  {activeRoutes !== 1 ? "s" : ""}
                </div>
              </div>

              {/* Total Requests */}
              <div className="stat-card">
                <div className="stat-header">
                  <span className="stat-label">TOTAL REQUESTS</span>

                  <span className="stat-icon purple">↗</span>
                </div>

                <div className="stat-value">{totalRequests}</div>

                <div className="stat-footer positive">
                  ↑ {successPercentage}% successful
                </div>
              </div>

              {/* Successful Requests */}
              <div className="stat-card">
                <div className="stat-header">
                  <span className="stat-label">SUCCESSFUL REQUESTS</span>

                  <span className="stat-icon green-bg">✓</span>
                </div>

                <div className="stat-value">{successfulRequests}</div>

                <div className="stat-footer positive">2xx / 3xx responses</div>
              </div>

              {/* Failed Requests */}
              <div className="stat-card">
                <div className="stat-header">
                  <span className="stat-label">FAILED REQUESTS</span>

                  <span className="stat-icon red">!</span>
                </div>

                <div className="stat-value">{failedRequests}</div>

                <div className="stat-footer">
                  {failedRequests === 0
                    ? "No failures detected"
                    : `${failedRequests} failed request${
                        failedRequests !== 1 ? "s" : ""
                      }`}
                </div>
              </div>
            </section>

            {/* Middle section */}
            <section className="dashboard-grid">
              {/* Traffic */}
              <div className="panel traffic-panel">
                <div className="panel-header">
                  <div>
                    <h3>API Traffic</h3>

                    <p>Request activity over the last {trafficRange} days</p>
                  </div>

                  <select
                    value={trafficRange}
                    onChange={(e) => setTrafficRange(Number(e.target.value))}
                  >
                    <option value="7">Last 7 days</option>
                    <option value="30">Last 30 days</option>
                  </select>
                </div>

                <div className="chart">
                  <div className="chart-y">
                    <span>{maxTraffic}</span>
                    <span>{Math.round(maxTraffic * 0.75)}</span>
                    <span>{Math.round(maxTraffic * 0.5)}</span>
                    <span>{Math.round(maxTraffic * 0.25)}</span>
                    <span>0</span>
                  </div>

                  <div
                    className={`chart-area ${
                      trafficRange === 30 ? "chart-scroll" : ""
                    }`}
                  >
                    <div className="chart-content">
                      <div className="grid-line"></div>
                      <div className="grid-line"></div>
                      <div className="grid-line"></div>
                      <div className="grid-line"></div>

                      <svg
                        viewBox="0 0 700 230"
                        preserveAspectRatio="none"
                        className="line-chart"
                      >
                        <defs>
                          <linearGradient
                            id="chartFill"
                            x1="0"
                            y1="0"
                            x2="0"
                            y2="1"
                          >
                            <stop offset="0%" stopOpacity="0.22" />

                            <stop offset="100%" stopOpacity="0" />
                          </linearGradient>
                        </defs>

                        {/* Real traffic area */}
                        <path
                          d={(() => {
                            const points = trafficData.map((item, index) => {
                              const x =
                                (index / (trafficData.length - 1 || 1)) * 700;

                              const y = 210 - (item.count / maxTraffic) * 170;

                              return `${x} ${y}`;
                            });

                            const line = points
                              .map((point, index) =>
                                index === 0 ? `M${point}` : `L${point}`
                              )
                              .join(" ");

                            return `${line} L700 230 L0 230 Z`;
                          })()}
                          fill="url(#chartFill)"
                        />

                        {/* Real traffic line */}
                        <path
                          d={(() => {
                            const points = trafficData.map((item, index) => {
                              const x =
                                (index / (trafficData.length - 1 || 1)) * 700;

                              const y = 210 - (item.count / maxTraffic) * 170;

                              return `${x} ${y}`;
                            });

                            return points
                              .map((point, index) =>
                                index === 0 ? `M${point}` : `L${point}`
                              )
                              .join(" ");
                          })()}
                          fill="none"
                          stroke="currentColor"
                          strokeWidth="3"
                        />
                      </svg>

                      <div className="chart-x">
                        {trafficData.map((item) => (
                          <span key={item.day}>{item.day}</span>
                        ))}
                      </div>
                    </div>
                  </div>
                </div>

                {totalRequests === 0 && (
                  <div
                    style={{
                      textAlign: "center",
                      marginTop: "10px",
                      fontSize: "12px",
                      color: "#9ca3af",
                    }}
                  >
                    No API traffic recorded yet
                  </div>
                )}
              </div>

              {/* System status */}
              <div className="panel">
                <div className="panel-header">
                  <div>
                    <h3>System Status</h3>

                    <p>Service health</p>
                  </div>

                  <span className="live-badge">
                    <span></span> LIVE
                  </span>
                </div>

                <div className="service-list">
                  <div className="service">
                    <div className="service-icon">API</div>

                    <div className="service-info">
                      <strong>API Management</strong>

                      <span>Port 8081</span>
                    </div>

                    <span className="service-status">
                      <i></i> Healthy
                    </span>
                  </div>

                  <div className="service">
                    <div className="service-icon">GW</div>

                    <div className="service-info">
                      <strong>Smart Gateway</strong>

                      <span>Port 8080</span>
                    </div>

                    <span className="service-status">
                      <i></i> Healthy
                    </span>
                  </div>

                  <div className="service">
                    <div className="service-icon">AN</div>

                    <div className="service-info">
                      <strong>Analytics</strong>

                      <span>Port 8082</span>
                    </div>

                    <span className="service-status">
                      <i></i> Healthy
                    </span>
                  </div>

                  <div className="service">
                    <div className="service-icon">DB</div>

                    <div className="service-info">
                      <strong>MongoDB</strong>

                      <span>Analytics Storage</span>
                    </div>

                    <span className="service-status">
                      <i></i> Healthy
                    </span>
                  </div>
                </div>
              </div>
            </section>

            {/* Recent activity */}
            <section className="panel activity-panel">
              <div className="panel-header">
                <div>
                  <h3>Recent API Activity</h3>

                  <p>Latest requests through your gateway</p>
                </div>

                <button
                  className="text-button"
                  onClick={() => setActivePage("Analytics")}
                >
                  View all →
                </button>
              </div>

              <div className="activity-table">
                <div className="table-head">
                  <span>ROUTE</span>
                  <span>METHOD</span>
                  <span>PATH</span>
                  <span>STATUS</span>
                  <span>RESPONSE</span>
                  <span>TIME</span>
                </div>

                {recentEvents.length > 0 ? (
                  recentEvents.map((event, index) => {
                    const isSuccess =
                      event.statusCode >= 200 && event.statusCode < 400;

                    return (
                      <div
                        className="table-row"
                        key={
                          event.id || event._id || `${event.timestamp}-${index}`
                        }
                      >
                        <div className="route-cell">
                          <div className="route-symbol">⇄</div>

                          <div>
                            <strong>{getRouteName(event.routeId)}</strong>

                            <small>{event.routeId || "-"}</small>
                          </div>
                        </div>

                        <span
                          className={`method ${
                            event.method?.toLowerCase() || ""
                          }`}
                        >
                          {event.method || "-"}
                        </span>

                        <span className="path-cell">{event.path || "-"}</span>

                        <span
                          className={
                            isSuccess ? "success-status" : "success-status"
                          }
                        >
                          {event.statusCode || "-"}
                          {event.statusCode
                            ? isSuccess
                              ? " OK"
                              : " Error"
                            : ""}
                        </span>

                        <span>
                          {event.responseTimeMs != null
                            ? `${event.responseTimeMs} ms`
                            : "-"}
                        </span>

                        <span className="time-cell">
                          {formatTime(event.timestamp)}
                        </span>
                      </div>
                    );
                  })
                ) : (
                  <div className="table-row">
                    <span
                      style={{
                        gridColumn: "1 / -1",
                        color: "#9ca3af",
                      }}
                    >
                      No API activity recorded yet.
                    </span>
                  </div>
                )}
              </div>
            </section>
          </div>
        )}

        {/* =========================
    ROUTES PAGE
    ========================= */}
        {activePage === "Routes" && (
          <div className="page-content">
            <div className="welcome-row">
              <div>
                <h2>Routes</h2>

                <p>Manage your Gatekeeper routes from this workspace.</p>
              </div>

              <button
                className="primary-button"
                onClick={() => {
                  setShowCreateRoute(true);
                  setRouteError("");
                }}
              >
                + Create Route
              </button>
            </div>

            {showCreateRoute && (
              <div className="create-route-card">
                <div className="create-route-header">
                  <div>
                    <h2>Create Route</h2>

                    <p>Register a new API route for your organization.</p>
                  </div>

                  <button
                    className="secondary-button"
                    onClick={() => {
                      setShowCreateRoute(false);
                      setRouteError("");
                    }}
                  >
                    Cancel
                  </button>
                </div>

                <form onSubmit={handleCreateRoute} className="route-form">
                  <div className="route-form-grid">
                    <div className="form-group">
                      <label>Route ID</label>

                      <input
                        type="text"
                        placeholder="e.g. users-api"
                        value={routeForm.routeId}
                        onChange={(e) =>
                          setRouteForm({
                            ...routeForm,
                            routeId: e.target.value,
                          })
                        }
                        required
                      />
                    </div>

                    <div className="form-group">
                      <label>Route Name</label>

                      <input
                        type="text"
                        placeholder="e.g. Users API"
                        value={routeForm.name}
                        onChange={(e) =>
                          setRouteForm({
                            ...routeForm,
                            name: e.target.value,
                          })
                        }
                        required
                      />
                    </div>

                    <div className="form-group">
                      <label>Path Pattern</label>

                      <input
                        type="text"
                        placeholder="/users/**"
                        value={routeForm.pathPattern}
                        onChange={(e) =>
                          setRouteForm({
                            ...routeForm,
                            pathPattern: e.target.value,
                          })
                        }
                        required
                      />
                    </div>

                    <div className="form-group">
                      <label>Target Base URL</label>

                      <input
                        type="url"
                        placeholder="https://api.example.com"
                        value={routeForm.targetBaseUrl}
                        onChange={(e) =>
                          setRouteForm({
                            ...routeForm,
                            targetBaseUrl: e.target.value,
                          })
                        }
                        required
                      />
                    </div>

                    <div className="form-group">
                      <label>Target Path Prefix</label>

                      <input
                        type="text"
                        placeholder="/users"
                        value={routeForm.targetPathPrefix}
                        onChange={(e) =>
                          setRouteForm({
                            ...routeForm,
                            targetPathPrefix: e.target.value,
                          })
                        }
                      />
                    </div>

                    <div className="form-group">
                      <label>Requests Per Minute</label>

                      <input
                        type="number"
                        min="1"
                        value={routeForm.requestsPerMinute}
                        onChange={(e) =>
                          setRouteForm({
                            ...routeForm,
                            requestsPerMinute: e.target.value,
                          })
                        }
                        required
                      />
                    </div>

                    <div className="form-group">
                      <label>Response Timeout (ms)</label>

                      <input
                        type="number"
                        min="100"
                        value={routeForm.responseTimeoutMs}
                        onChange={(e) =>
                          setRouteForm({
                            ...routeForm,
                            responseTimeoutMs: e.target.value,
                          })
                        }
                        required
                      />
                    </div>

                    <div className="form-group checkbox-group">
                      <label>
                        <input
                          type="checkbox"
                          checked={routeForm.idempotencyEnabled}
                          onChange={(e) =>
                            setRouteForm({
                              ...routeForm,
                              idempotencyEnabled: e.target.checked,
                            })
                          }
                        />
                        Enable Idempotency
                      </label>

                      <label>
                        <input
                          type="checkbox"
                          checked={routeForm.active}
                          onChange={(e) =>
                            setRouteForm({
                              ...routeForm,
                              active: e.target.checked,
                            })
                          }
                        />
                        Activate Route
                      </label>
                    </div>
                  </div>

                  {routeError && (
                    <div className="login-error">{routeError}</div>
                  )}

                  <button
                    type="submit"
                    className="primary-button"
                    disabled={creatingRoute}
                  >
                    {creatingRoute ? "Creating..." : "Create Route"}
                  </button>
                </form>
              </div>
            )}

            <div className="panel activity-panel">
              <div className="panel-header">
                <div>
                  <h3>Configured Routes</h3>

                  <p>Routes currently registered for your organization.</p>
                </div>
              </div>

              <div className="activity-table">
                <div className="table-head">
                  <span>ROUTE</span>

                  <span>PATH</span>

                  <span>TARGET</span>

                  <span>STATUS</span>

                  <span>ACTION</span>
                </div>

                {routes.length > 0 ? (
                  routes.map((route) => (
                    <div className="table-row" key={route.id || route.routeId}>
                      <div className="route-cell">
                        <div className="route-symbol">⇄</div>

                        <div>
                          <strong>{route.name || "Unnamed Route"}</strong>

                          <small>{route.routeId || "-"}</small>
                        </div>
                      </div>

                      <span className="path-cell">
                        {route.pathPattern || "-"}
                      </span>

                      <span className="path-cell">
                        {route.targetBaseUrl || "-"}
                      </span>

                      <span
                        className={
                          route.active ? "success-status" : "inactive-status"
                        }
                      >
                        {route.active ? "Active" : "Inactive"}
                      </span>
                      <button
                        className="edit-route-button"
                        onClick={() => handleEditRoute(route)}
                      >
                        Edit
                      </button>

                      <button
                        className="delete-route-button"
                        onClick={() => handleDeleteRoute(route.routeId)}
                      >
                        Delete
                      </button>
                    </div>
                  ))
                ) : (
                  <div className="table-row">
                    <span
                      style={{
                        gridColumn: "1 / -1",
                        color: "#9ca3af",
                      }}
                    >
                      No routes found.
                    </span>
                  </div>
                )}
              </div>
            </div>
          </div>
        )}

        {/* =========================
    HEADER RULES PAGE
    ========================= */}

        {activePage === "Header Rules" && (
          <div className="page-content">
            <div className="welcome-row">
              <div>
                <h2>Header Rules</h2>

                <p>Manage request and response header rules.</p>
              </div>

              <button
                className="primary-button"
                onClick={() => setShowHeaderRuleForm(true)}
              >
                + Add Header Rule
              </button>
            </div>
            {showHeaderRuleForm && (
              <div className="route-form-panel">
                <h3>Add Header Rule</h3>

                <p>Configure a request or response header rule.</p>

                <div className="form-grid">
                  <div className="form-group">
                    <label>Route</label>

                    <select
                      value={headerRuleForm.routeId}
                      onChange={(e) =>
                        setHeaderRuleForm({
                          ...headerRuleForm,
                          routeId: e.target.value,
                        })
                      }
                    >
                      {routes.map((route) => (
                        <option key={route.routeId} value={route.routeId}>
                          {route.routeId}
                        </option>
                      ))}
                    </select>
                  </div>

                  <div className="form-group">
                    <label>Direction</label>

                    <select
                      value={headerRuleForm.direction}
                      onChange={(e) =>
                        setHeaderRuleForm({
                          ...headerRuleForm,
                          direction: e.target.value,
                        })
                      }
                    >
                      <option value="REQUEST">REQUEST</option>
                      <option value="RESPONSE">RESPONSE</option>
                    </select>
                  </div>

                  <div className="form-group">
                    <label>Action</label>

                    <select
                      value={headerRuleForm.action}
                      onChange={(e) =>
                        setHeaderRuleForm({
                          ...headerRuleForm,
                          action: e.target.value,
                        })
                      }
                    >
                      <option value="ADD">ADD</option>
                      <option value="SET">SET</option>
                      <option value="REMOVE">REMOVE</option>
                    </select>
                  </div>

                  <div className="form-group">
                    <label>Header Name</label>

                    <input
                      type="text"
                      value={headerRuleForm.headerName}
                      onChange={(e) =>
                        setHeaderRuleForm({
                          ...headerRuleForm,
                          headerName: e.target.value,
                        })
                      }
                      placeholder="X-Custom-Header"
                    />
                  </div>

                  <div className="form-group">
                    <label>Header Value</label>

                    <input
                      type="text"
                      value={headerRuleForm.headerValue}
                      onChange={(e) =>
                        setHeaderRuleForm({
                          ...headerRuleForm,
                          headerValue: e.target.value,
                        })
                      }
                      placeholder="example-value"
                    />
                  </div>
                </div>

                <div className="form-actions">
                  <button
                    type="button"
                    className="secondary-button"
                    onClick={() => setShowHeaderRuleForm(false)}
                  >
                    Cancel
                  </button>

                  <button
                    type="button"
                    className="primary-button"
                    onClick={handleAddHeaderRule}
                  >
                    Add Rule
                  </button>
                </div>
              </div>
            )}

            <div className="activity-table">
              <div className="table-head">
                <span>ROUTE</span>
                <span>DIRECTION</span>
                <span>ACTION</span>
                <span>HEADER</span>
                <span>VALUE</span>
                <span>ACTION</span>
              </div>

              {headerRules.length > 0 ? (
                headerRules.map((rule) => {
                  const route = routes.find(
                    (r) => String(r.id) === String(rule.routeId)
                  );

                  return (
                    <div className="table-row" key={rule.id}>
                      <span>{route?.routeId || rule.routeId}</span>

                      <span>{rule.direction}</span>

                      <span>{rule.action}</span>

                      <span>{rule.headerName}</span>

                      <span>{rule.headerValue}</span>

                      <button
                        type="button"
                        className="secondary-button"
                        onClick={() =>
                          handleDeleteHeaderRule(
                            route?.routeId || rule.routeId,
                            rule.id
                          )
                        }
                      >
                        Delete
                      </button>
                    </div>
                  );
                })
              ) : (
                <div className="table-row">
                  <span
                    style={{
                      gridColumn: "1 / -1",
                      color: "#9ca3af",
                    }}
                  >
                    No header rules found.
                  </span>
                </div>
              )}
            </div>
          </div>
        )}

        {/* =========================
    ANALYTICS PAGE
    ========================= */}

        {activePage === "Analytics" && (
          <div className="page-content">
            <div className="welcome-row">
              <div>
                <h2>Analytics</h2>

                <p>Monitor API usage and request performance.</p>
              </div>
            </div>

            {/* Analytics Stats */}
            <section className="stats-grid">
              {/* Total Requests */}
              <div className="stat-card">
                <div className="stat-header">
                  <span className="stat-label">TOTAL REQUESTS</span>

                  <span className="stat-icon purple">↗</span>
                </div>

                <div className="stat-value">
                  {analyticsSummary?.totalRequests ?? 0}
                </div>

                <div className="stat-footer">All recorded API requests</div>
              </div>

              {/* Successful */}
              <div className="stat-card">
                <div className="stat-header">
                  <span className="stat-label">SUCCESSFUL REQUESTS</span>

                  <span className="stat-icon green-bg">✓</span>
                </div>

                <div className="stat-value">
                  {analyticsSummary?.successfulRequests ?? 0}
                </div>

                <div className="stat-footer positive">2xx / 3xx responses</div>
              </div>

              {/* Failed */}
              <div className="stat-card">
                <div className="stat-header">
                  <span className="stat-label">FAILED REQUESTS</span>

                  <span className="stat-icon red">!</span>
                </div>

                <div className="stat-value">
                  {analyticsSummary?.failedRequests ?? 0}
                </div>

                <div className="stat-footer">
                  {(analyticsSummary?.failedRequests ?? 0) === 0
                    ? "No failures detected"
                    : "Failed API requests"}
                </div>
              </div>

              {/* Success Rate */}
              <div className="stat-card">
                <div className="stat-header">
                  <span className="stat-label">SUCCESS RATE</span>

                  <span className="stat-icon blue">%</span>
                </div>

                <div className="stat-value">
                  {analyticsSummary?.totalRequests
                    ? Math.round(
                        (analyticsSummary.successfulRequests /
                          analyticsSummary.totalRequests) *
                          100
                      )
                    : 0}
                  %
                </div>

                <div className="stat-footer positive">
                  Overall request success
                </div>
              </div>
            </section>

            {/* Recent API Events */}
            <section className="panel activity-panel">
              <div className="panel-header">
                <div>
                  <h3>API Request History</h3>

                  <p>Recent requests recorded by the Analytics Service.</p>
                </div>

                <span className="live-badge">
                  <span></span> LIVE DATA
                </span>
              </div>

              <div className="activity-table">
                <div className="table-head">
                  <span>ROUTE</span>
                  <span>METHOD</span>
                  <span>PATH</span>
                  <span>STATUS</span>
                  <span>RESPONSE</span>
                  <span>TIME</span>
                </div>

                {analyticsEvents.length > 0 ? (
                  analyticsEvents.map((event, index) => {
                    const isSuccess =
                      event.statusCode >= 200 && event.statusCode < 400;

                    return (
                      <div
                        className="table-row"
                        key={
                          event.id || event._id || `${event.timestamp}-${index}`
                        }
                      >
                        <div className="route-cell">
                          <div className="route-symbol">⇄</div>

                          <div>
                            <strong>{getRouteName(event.routeId)}</strong>

                            <small>{event.routeId || "-"}</small>
                          </div>
                        </div>

                        <span
                          className={`method ${
                            event.method?.toLowerCase() || ""
                          }`}
                        >
                          {event.method || "-"}
                        </span>

                        <span className="path-cell">{event.path || "-"}</span>

                        <span
                          className={
                            isSuccess ? "success-status" : "error-status"
                          }
                        >
                          {event.statusCode || "-"}

                          {event.statusCode
                            ? isSuccess
                              ? " OK"
                              : " Error"
                            : ""}
                        </span>

                        <span>
                          {event.responseTimeMs != null
                            ? `${event.responseTimeMs} ms`
                            : "-"}
                        </span>

                        <span className="time-cell">
                          {formatTime(event.timestamp)}
                        </span>
                      </div>
                    );
                  })
                ) : (
                  <div className="table-row">
                    <span
                      style={{
                        gridColumn: "1 / -1",
                        color: "#9ca3af",
                      }}
                    >
                      No API activity recorded yet.
                    </span>
                  </div>
                )}
              </div>
            </section>
          </div>
        )}

        {/* =========================
    AUDIT LOGS PAGE
    ========================= */}

        {activePage === "Audit Logs" && (
          <div className="page-content">
            <div className="welcome-row">
              <div>
                <h2>Audit Logs</h2>

                <p>Review administrative activity in your organization.</p>
              </div>
            </div>

            <div className="activity-section">
              <div className="section-header">
                <div>
                  <h3>Administrative Activity</h3>

                  <p>Recent changes made to your API configuration.</p>
                </div>
              </div>

              <div className="activity-table">
                <div className="table-head">
                  <span>ACTION</span>
                  <span>RESOURCE</span>
                  <span>RESOURCE ID</span>
                  <span>ACTOR</span>
                  <span>TIME</span>
                </div>

                {auditRecords.length > 0 ? (
                  auditRecords.map((record, index) => (
                    <div className="table-row" key={record.id || index}>
                      <span>{record.action || "-"}</span>

                      <span>{record.resourceType || "-"}</span>

                      <span>{record.resourceId || "-"}</span>

                      <span>
                        {record.actorUsername || record.username || "-"}
                      </span>

                      <span>
                        {record.createdAt
                          ? new Date(record.createdAt).toLocaleString()
                          : "-"}
                      </span>
                    </div>
                  ))
                ) : (
                  <div className="table-row">
                    <span
                      style={{
                        gridColumn: "1 / -1",
                        color: "#9ca3af",
                      }}
                    >
                      No audit records found.
                    </span>
                  </div>
                )}
              </div>
            </div>
          </div>
        )}
      </main>
    </div>
  );
}

export default App;
