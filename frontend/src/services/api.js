const MANAGEMENT_URL = 'http://localhost:8081'
const ANALYTICS_URL = 'http://localhost:8082'
const GATEWAY_URL = 'http://localhost:8080'

const ORGANIZATION_ID = 4

const getToken = () => {
  return localStorage.getItem('gatekeeper_token')
}

const request = async (url, options = {}) => {
  const token = getToken()

  const response = await fetch(url, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...(options.headers || {}),
    },
  })

  const text = await response.text()

  let data = null

  if (text) {
    try {
      data = JSON.parse(text)
    } catch {
      data = null
    }
  }

  if (!response.ok) {
    throw new Error(
      data?.message ||
        text ||
        `Request failed with status ${response.status}`
    )
  }

  return data
}

// =========================
// Routes
// =========================

export const getRoutes = () => {
  return request(
    `${MANAGEMENT_URL}/api/organizations/${ORGANIZATION_ID}/routes`
  )
}

export const createRoute = (routeData) => {
  return request(
    `${MANAGEMENT_URL}/api/organizations/${ORGANIZATION_ID}/routes`,
    {
      method: 'POST',
      body: JSON.stringify(routeData),
    }
  )
}

export const updateRoute = (routeId, routeData) => {
  return request(
    `${MANAGEMENT_URL}/api/organizations/${ORGANIZATION_ID}/routes/${routeId}`,
    {
      method: 'PUT',
      body: JSON.stringify(routeData),
    }
  )
}

export const deleteRoute = (routeId) => {
  return request(
    `${MANAGEMENT_URL}/api/organizations/${ORGANIZATION_ID}/routes/${routeId}`,
    {
      method: 'DELETE',
    }
  )
}

// =========================
// Header Rules
// =========================

export const getHeaderRules = (routeId) => {
  return request(
    `${MANAGEMENT_URL}/api/organizations/${ORGANIZATION_ID}/routes/${routeId}/headers`
  )
}

export const createHeaderRule = (routeId, ruleData) => {
  return request(
    `${MANAGEMENT_URL}/api/organizations/${ORGANIZATION_ID}/routes/${routeId}/headers`,
    {
      method: 'POST',
      body: JSON.stringify(ruleData),
    }
  )
}

export const deleteHeaderRule = (routeId, ruleId) => {
  return request(
    `${MANAGEMENT_URL}/api/organizations/${ORGANIZATION_ID}/routes/${routeId}/headers/${ruleId}`,
    {
      method: 'DELETE',
    }
  )
}

// =========================
// Analytics
// =========================

export const getAnalyticsSummary = () => {
  return request(
    `${ANALYTICS_URL}/api/analytics/organizations/${ORGANIZATION_ID}/summary`
  )
}

export const getAnalyticsEvents = () => {
  return request(
    `${ANALYTICS_URL}/api/analytics/organizations/${ORGANIZATION_ID}/events`
  )
}

// =========================
// Audit Records
// =========================

export const getAuditRecords = () => {
  return request(
    `${MANAGEMENT_URL}/api/organizations/${ORGANIZATION_ID}/audit-records`
  )
}

// =========================
// Gateway
// =========================

export { GATEWAY_URL }