package com.imigishalink.controller;

import java.util.*;

public class ApiEndpointsDocumentation {
    
    public static final String BASE_URL = "http://localhost:8080/api/v1";
    public static final String AUTH_BASE_URL = "http://localhost:8080/api/v1/auth";
    public static final String OTP_AUTH_BASE_URL = "http://localhost:8080/api/auth";
    
    /**
     * Represents an API endpoint with all its details
     */
    public static class Endpoint {
        private final String method;
        private final String path;
        private final String description;
        private final boolean requiresAuth;
        private final List<String> requiredRoles;
        private final Map<String, String> queryParams;
        private final String requestBody;
        private final String responseType;
        
        public Endpoint(String method, String path, String description, 
                       boolean requiresAuth, List<String> requiredRoles,
                       Map<String, String> queryParams, String requestBody, String responseType) {
            this.method = method;
            this.path = path;
            this.description = description;
            this.requiresAuth = requiresAuth;
            this.requiredRoles = requiredRoles != null ? requiredRoles : new ArrayList<>();
            this.queryParams = queryParams != null ? queryParams : new HashMap<>();
            this.requestBody = requestBody;
            this.responseType = responseType;
        }
        
        public String getMethod() { return method; }
        public String getPath() { return path; }
        public String getDescription() { return description; }
        public boolean requiresAuth() { return requiresAuth; }
        public List<String> getRequiredRoles() { return requiredRoles; }
        public Map<String, String> getQueryParams() { return queryParams; }
        public String getRequestBody() { return requestBody; }
        public String getResponseType() { return responseType; }
        public String getFullUrl() { return BASE_URL + path; }
        
        @Override
        public String toString() {
            return String.format("%s %s - %s", method, path, description);
        }
    }
    
    /**
     * Get all authentication endpoints
     */
    public static List<Endpoint> getAuthEndpoints() {
        List<Endpoint> endpoints = new ArrayList<>();
        
        endpoints.add(new Endpoint("POST", "/auth/register",
            "Register a new user (USER or NGO role)",
            false, null,
            null,
            "{ firstName, lastName, email, password, phoneNumber, role, locationId, province, district, sector, cell, village }",
            "AuthResponse"));
        
        endpoints.add(new Endpoint("POST", "/auth/login",
            "Step 1: Login with email and password, receive OTP challenge",
            false, null,
            null,
            "{ email, password, otpEmail (optional) }",
            "{ challengeId, message }"));
        
        endpoints.add(new Endpoint("POST", "/auth/login/verify-otp",
            "Step 2: Verify OTP and complete login",
            false, null,
            null,
            "{ challengeId, code }",
            "AuthResponse"));
        
        endpoints.add(new Endpoint("POST", "/auth/refresh",
            "Refresh access token using refresh token",
            false, null,
            null,
            "{ refreshToken }",
            "AuthResponse"));
        
        endpoints.add(new Endpoint("GET", "/auth/verify",
            "Verify email with verification code",
            false, null,
            Map.of("code", "verificationCode"),
            null,
            "Success message"));
        
        endpoints.add(new Endpoint("POST", "/auth/forgot-password",
            "Request password reset",
            false, null,
            null,
            "{ email }",
            "Success message"));
        
        endpoints.add(new Endpoint("POST", "/auth/reset-password",
            "Reset password with token",
            false, null,
            null,
            "{ token, newPassword }",
            "Success message"));
        
        endpoints.add(new Endpoint("POST", "/auth/admin/verify-user",
            "Admin: Manually verify a user",
            true, List.of("ADMIN"),
            Map.of("email", "userEmail"),
            null,
            "Success message"));
        
        endpoints.add(new Endpoint("POST", "/auth/admin/create-admin",
            "Admin: Create an admin user",
            true, List.of("ADMIN"),
            null,
            "{ firstName, lastName, email, password, phoneNumber }",
            "AuthResponse"));
        
        return endpoints;
    }
    
    /**
     * Get all OTP authentication endpoints (alternative)
     */
    public static List<Endpoint> getOTPAuthEndpoints() {
        List<Endpoint> endpoints = new ArrayList<>();
        
        endpoints.add(new Endpoint("POST", "/api/auth/login",
            "Step 1: Login with email and password, receive 6-digit OTP",
            false, null,
            null,
            "{ email, password, otpEmail (optional) }",
            "{ message, email, loginEmail }"));
        
        endpoints.add(new Endpoint("POST", "/api/auth/login/verify-otp",
            "Step 2: Verify 6-digit OTP and complete login",
            false, null,
            null,
            "{ email, otp, loginEmail (optional) }",
            "AuthResponse"));
        
        return endpoints;
    }
    
    /**
     * Get all user endpoints
     */
    public static List<Endpoint> getUserEndpoints() {
        List<Endpoint> endpoints = new ArrayList<>();
        
        endpoints.add(new Endpoint("GET", "/users/me",
            "Get current authenticated user",
            true, null,
            null,
            null,
            "User"));
        
        endpoints.add(new Endpoint("PUT", "/users/me",
            "Update current user profile",
            true, null,
            null,
            "User object with updated fields",
            "User"));
        
        endpoints.add(new Endpoint("GET", "/users",
            "Get all users (paginated)",
            true, List.of("ADMIN"),
            Map.of("page", "page number", "size", "page size", "sortBy", "field", "direction", "asc|desc"),
            null,
            "PageResponse<User>"));
        
        endpoints.add(new Endpoint("GET", "/users/search",
            "Search users by query",
            false, null,
            Map.of("query", "searchTerm", "page", "page number", "size", "page size"),
            null,
            "PageResponse<User>"));
        
        endpoints.add(new Endpoint("GET", "/users/location",
            "Get users by location",
            false, null,
            Map.of("province", "province name", "district", "district name", "sector", "sector name", "page", "page number", "size", "page size"),
            null,
            "PageResponse<User>"));
        
        endpoints.add(new Endpoint("GET", "/users/{id}",
            "Get user by ID",
            false, null,
            null,
            null,
            "User"));
        
        endpoints.add(new Endpoint("PUT", "/users/{id}/location",
            "Update user location",
            true, null,
            Map.of("locationId", "location ID"),
            null,
            "Success message"));
        
        endpoints.add(new Endpoint("POST", "/users/{id}/follow",
            "Follow a user",
            true, null,
            null,
            null,
            "Success message"));
        
        endpoints.add(new Endpoint("POST", "/users/{id}/unfollow",
            "Unfollow a user",
            true, null,
            null,
            null,
            "Success message"));
        
        endpoints.add(new Endpoint("GET", "/users/{id}/followers",
            "Get user's followers",
            false, null,
            Map.of("page", "page number", "size", "page size"),
            null,
            "PageResponse<User>"));
        
        endpoints.add(new Endpoint("GET", "/users/{id}/following",
            "Get users that this user follows",
            false, null,
            Map.of("page", "page number", "size", "page size"),
            null,
            "PageResponse<User>"));
        
        endpoints.add(new Endpoint("GET", "/users/stats",
            "Get user statistics",
            true, List.of("ADMIN"),
            null,
            null,
            "{ totalUsers, totalAdmins, totalNgos, totalDonors, totalVerified }"));
        
        endpoints.add(new Endpoint("POST", "/users",
            "Create a new user",
            true, List.of("ADMIN"),
            null,
            "User object",
            "User"));
        
        endpoints.add(new Endpoint("PUT", "/users/{id}",
            "Update user by ID",
            true, List.of("ADMIN"),
            null,
            "User object with updated fields",
            "User"));
        
        endpoints.add(new Endpoint("DELETE", "/users/{id}",
            "Deactivate a user (soft delete)",
            true, List.of("ADMIN"),
            null,
            null,
            "Success message"));
        
        endpoints.add(new Endpoint("POST", "/users/assign-locations",
            "Assign locations to users without locations",
            true, List.of("ADMIN"),
            Map.of("reassignAll", "true|false"),
            null,
            "Success message"));
        
        return endpoints;
    }
    
    /**
     * Get all donation endpoints
     */
    public static List<Endpoint> getDonationEndpoints() {
        List<Endpoint> endpoints = new ArrayList<>();
        
        endpoints.add(new Endpoint("GET", "/donations",
            "Get all donations (paginated)",
            false, null,
            Map.of("page", "page number", "size", "page size", "status", "donation status", 
                   "type", "donation type", "province", "province name", "categoryId", "category ID", "search", "search term"),
            null,
            "PageResponse<Donation>"));
        
        endpoints.add(new Endpoint("GET", "/donations/urgent",
            "Get urgent donations",
            false, null,
            Map.of("page", "page number", "size", "page size"),
            null,
            "PageResponse<Donation>"));
        
        endpoints.add(new Endpoint("GET", "/donations/{id}",
            "Get donation by ID",
            false, null,
            null,
            null,
            "Donation"));
        
        endpoints.add(new Endpoint("POST", "/donations",
            "Create a new donation",
            true, null,
            null,
            "Donation object",
            "Donation"));
        
        endpoints.add(new Endpoint("PUT", "/donations/{id}",
            "Update donation",
            true, null,
            null,
            "Donation object with updated fields",
            "Donation"));
        
        endpoints.add(new Endpoint("POST", "/donations/{id}/contribute",
            "Contribute to a donation",
            true, null,
            null,
            "Contribution object",
            "Contribution"));
        
        endpoints.add(new Endpoint("GET", "/donations/my-donations",
            "Get current user's donations",
            true, null,
            Map.of("page", "page number", "size", "page size", "status", "donation status"),
            null,
            "PageResponse<Donation>"));
        
        endpoints.add(new Endpoint("GET", "/donations/stats",
            "Get donation statistics",
            false, null,
            null,
            null,
            "{ totalDonations, openDonations, fulfilledDonations, donationsByProvince, donationsByType }"));
        
        endpoints.add(new Endpoint("POST", "/donations/{id}/approve",
            "Approve a donation (assign to NGO)",
            true, List.of("ADMIN", "NGO"),
            null,
            "{ ngoId (optional for ADMIN) }",
            "Donation"));
        
        endpoints.add(new Endpoint("POST", "/donations/{id}/reject",
            "Reject a donation",
            true, List.of("ADMIN", "NGO"),
            null,
            "{ reason (optional), ngoId (optional for NGO users) }",
            "Donation"));
        
        endpoints.add(new Endpoint("POST", "/donations/{id}/schedule-pickup",
            "Schedule pickup for a donation",
            true, List.of("ADMIN", "NGO"),
            null,
            "{ pickupDate }",
            "Donation"));
        
        endpoints.add(new Endpoint("POST", "/donations/{id}/status",
            "Update donation status",
            true, null,
            Map.of("status", "donation status"),
            null,
            "Success message"));
        
        endpoints.add(new Endpoint("POST", "/donations/{id}/request-approval",
            "Request approval/rejection for a donation (requires admin confirmation)",
            true, List.of("NGO"),
            null,
            "{ requestType: APPROVE|REJECT, ngoId, reason }",
            "DonationApprovalRequest"));
        
        endpoints.add(new Endpoint("POST", "/donations/approval-requests/{requestId}/confirm",
            "Confirm or reject an approval request",
            true, List.of("ADMIN", "NGO"),
            null,
            "{ approved: boolean, adminNotes: string }",
            "Donation"));
        
        endpoints.add(new Endpoint("GET", "/donations/approval-requests/pending",
            "Get all pending approval requests",
            true, List.of("ADMIN"),
            Map.of("page", "page number", "size", "page size"),
            null,
            "PageResponse<DonationApprovalRequest>"));
        
        endpoints.add(new Endpoint("GET", "/donations/approval-requests/my-requests",
            "Get current user's approval requests",
            true, List.of("NGO"),
            Map.of("page", "page number", "size", "page size"),
            null,
            "PageResponse<DonationApprovalRequest>"));
        
        endpoints.add(new Endpoint("GET", "/donations/pending",
            "Get all pending donations (OPEN status)",
            true, List.of("NGO", "ADMIN"),
            Map.of("page", "page number", "size", "page size"),
            null,
            "PageResponse<Donation>"));
        
        return endpoints;
    }
    
    /**
     * Get all NGO endpoints
     */
    public static List<Endpoint> getNGOEndpoints() {
        List<Endpoint> endpoints = new ArrayList<>();
        
        endpoints.add(new Endpoint("GET", "/ngos",
            "Get all NGOs (paginated)",
            false, null,
            Map.of("page", "page number", "size", "page size", "verified", "true|false", 
                   "province", "province name", "categoryId", "category ID", "search", "search term"),
            null,
            "PageResponse<NGO>"));
        
        endpoints.add(new Endpoint("GET", "/ngos/top",
            "Get top NGOs by donations received",
            false, null,
            Map.of("page", "page number", "size", "page size"),
            null,
            "PageResponse<NGO>"));
        
        endpoints.add(new Endpoint("GET", "/ngos/{id}",
            "Get NGO by ID",
            false, null,
            null,
            null,
            "NGO"));
        
        endpoints.add(new Endpoint("POST", "/ngos/register",
            "Public registration endpoint - register an NGO",
            false, null,
            null,
            "NGO object (name, registrationNumber, email required)",
            "NGO"));
        
        endpoints.add(new Endpoint("POST", "/ngos",
            "Create an NGO (authenticated)",
            true, List.of("ADMIN", "NGO"),
            null,
            "NGO object",
            "NGO"));
        
        endpoints.add(new Endpoint("PUT", "/ngos/{id}",
            "Update NGO",
            true, null,
            null,
            "NGO object with updated fields",
            "NGO"));
        
        endpoints.add(new Endpoint("DELETE", "/ngos/{id}",
            "Delete an NGO",
            true, List.of("ADMIN"),
            null,
            null,
            "Success message"));
        
        endpoints.add(new Endpoint("POST", "/ngos/{id}/admins/{userId}",
            "Add admin to NGO",
            true, null,
            null,
            null,
            "Success message"));
        
        endpoints.add(new Endpoint("DELETE", "/ngos/{id}/admins/{userId}",
            "Remove admin from NGO",
            true, null,
            null,
            null,
            "Success message"));
        
        endpoints.add(new Endpoint("GET", "/ngos/stats",
            "Get NGO statistics",
            false, null,
            null,
            null,
            "{ totalNgos, verifiedNgos, unverifiedNgos, ngosByProvince }"));
        
        endpoints.add(new Endpoint("GET", "/ngos/my-ngos",
            "Get NGOs where current user is an admin",
            true, List.of("NGO"),
            Map.of("page", "page number", "size", "page size"),
            null,
            "PageResponse<NGO>"));
        
        return endpoints;
    }
    
    /**
     * Get all location endpoints
     */
    public static List<Endpoint> getLocationEndpoints() {
        List<Endpoint> endpoints = new ArrayList<>();
        
        endpoints.add(new Endpoint("GET", "/locations/provinces",
            "Get all provinces",
            false, null,
            null,
            null,
            "List<String>"));
        
        endpoints.add(new Endpoint("GET", "/locations/districts",
            "Get districts by province",
            false, null,
            Map.of("province", "province name"),
            null,
            "List<String>"));
        
        endpoints.add(new Endpoint("GET", "/locations/sectors",
            "Get sectors by district",
            false, null,
            Map.of("district", "district name", "province", "province name (optional)"),
            null,
            "List<String>"));
        
        endpoints.add(new Endpoint("GET", "/locations/cells",
            "Get cells by sector",
            false, null,
            Map.of("sector", "sector name", "district", "district name (optional)", "province", "province name (optional)"),
            null,
            "List<String>"));
        
        endpoints.add(new Endpoint("GET", "/locations/villages",
            "Get villages by cell",
            false, null,
            Map.of("cell", "cell name", "sector", "sector name (optional)", 
                   "district", "district name (optional)", "province", "province name (optional)"),
            null,
            "List<String>"));
        
        endpoints.add(new Endpoint("GET", "/locations",
            "Get all locations (paginated)",
            false, null,
            Map.of("page", "page number", "size", "page size", "province", "province name", 
                   "district", "district name", "search", "search term"),
            null,
            "PageResponse<Location>"));
        
        endpoints.add(new Endpoint("GET", "/locations/{id}",
            "Get location by ID",
            false, null,
            null,
            null,
            "Location"));
        
        endpoints.add(new Endpoint("GET", "/locations/{id}/stats",
            "Get location statistics",
            false, null,
            null,
            null,
            "{ location, totalUsers, totalNgos, totalDonations, subLocationsCount }"));
        
        endpoints.add(new Endpoint("GET", "/locations/hierarchy",
            "Get location hierarchy (province -> district -> sectors)",
            false, null,
            null,
            null,
            "Map<String, Map<String, List<String>>>"));
        
        return endpoints;
    }
    
    /**
     * Get all search endpoints
     */
    public static List<Endpoint> getSearchEndpoints() {
        List<Endpoint> endpoints = new ArrayList<>();
        
        endpoints.add(new Endpoint("GET", "/search/all",
            "Search across all entities (users, NGOs, donations, communities, categories)",
            true, null,
            Map.of("query", "search term", "page", "page number", "size", "page size"),
            null,
            "SearchResult"));
        
        endpoints.add(new Endpoint("POST", "/search/advanced",
            "Advanced search with multiple criteria",
            false, null,
            Map.of("page", "page number", "size", "page size"),
            "SearchCriteria object",
            "SearchResult"));
        
        endpoints.add(new Endpoint("GET", "/search/users/location",
            "Search users by location",
            false, null,
            Map.of("province", "province name", "district", "district name", 
                   "sector", "sector name", "page", "page number", "size", "page size"),
            null,
            "PageResponse<User>"));
        
        endpoints.add(new Endpoint("GET", "/search/ngos",
            "Search NGOs",
            false, null,
            Map.of("province", "province name", "categoryId", "category ID", 
                   "verified", "true|false", "page", "page number", "size", "page size"),
            null,
            "PageResponse<NGO>"));
        
        endpoints.add(new Endpoint("GET", "/search/donations",
            "Search donations",
            false, null,
            Map.of("search", "search term", "province", "province name", "categoryId", "category ID", 
                   "status", "donation status", "page", "page number", "size", "page size"),
            null,
            "PageResponse<Donation>"));
        
        endpoints.add(new Endpoint("GET", "/search/donations/urgent",
            "Find urgent donations",
            false, null,
            Map.of("province", "province name", "page", "page number", "size", "page size"),
            null,
            "PageResponse<Donation>"));
        
        endpoints.add(new Endpoint("GET", "/search/suggestions",
            "Get search suggestions",
            true, null,
            Map.of("query", "search term"),
            null,
            "SearchSuggestions"));
        
        endpoints.add(new Endpoint("GET", "/search/analytics",
            "Get search analytics",
            false, null,
            null,
            null,
            "{ totalSearches, topQueries, searchTrends }"));
        
        endpoints.add(new Endpoint("GET", "/search/nearby/users",
            "Find nearby users",
            false, null,
            Map.of("userId", "user ID", "radiusKm", "radius in km", "page", "page number", "size", "page size"),
            null,
            "PageResponse<User>"));
        
        endpoints.add(new Endpoint("GET", "/search/donors/category",
            "Find donors by category",
            false, null,
            Map.of("categoryId", "category ID", "province", "province name", 
                   "page", "page number", "size", "page size"),
            null,
            "PageResponse<User>"));
        
        return endpoints;
    }
    
    /**
     * Get all category endpoints
     */
    public static List<Endpoint> getCategoryEndpoints() {
        List<Endpoint> endpoints = new ArrayList<>();
        
        endpoints.add(new Endpoint("GET", "/categories",
            "Get all categories (paginated)",
            false, null,
            Map.of("page", "page number", "size", "page size", "active", "true|false", "search", "search term"),
            null,
            "PageResponse<Category>"));
        
        endpoints.add(new Endpoint("GET", "/categories/root",
            "Get root categories (no parent)",
            false, null,
            Map.of("page", "page number", "size", "page size"),
            null,
            "PageResponse<Category>"));
        
        endpoints.add(new Endpoint("GET", "/categories/{id}",
            "Get category by ID",
            false, null,
            null,
            null,
            "Category"));
        
        endpoints.add(new Endpoint("GET", "/categories/{id}/subcategories",
            "Get subcategories of a category",
            false, null,
            null,
            null,
            "List<Category>"));
        
        endpoints.add(new Endpoint("POST", "/categories",
            "Create a category",
            true, List.of("ADMIN"),
            null,
            "Category object",
            "Category"));
        
        endpoints.add(new Endpoint("PUT", "/categories/{id}",
            "Update category",
            true, List.of("ADMIN"),
            null,
            "Category object with updated fields",
            "Category"));
        
        endpoints.add(new Endpoint("GET", "/categories/{id}/stats",
            "Get category statistics",
            false, null,
            null,
            null,
            "{ category, totalDonations, totalNgos, subCategoriesCount }"));
        
        endpoints.add(new Endpoint("DELETE", "/categories/{id}",
            "Deactivate a category (soft delete)",
            true, List.of("ADMIN"),
            null,
            null,
            "Success message"));
        
        return endpoints;
    }
    
    /**
     * Get all message endpoints
     */
    public static List<Endpoint> getMessageEndpoints() {
        List<Endpoint> endpoints = new ArrayList<>();
        
        endpoints.add(new Endpoint("GET", "/messages/conversation/{userId}",
            "Get conversation with a user",
            true, null,
            Map.of("page", "page number", "size", "page size"),
            null,
            "PageResponse<Message>"));
        
        endpoints.add(new Endpoint("GET", "/messages/community/{communityId}",
            "Get community messages",
            true, null,
            Map.of("page", "page number", "size", "page size"),
            null,
            "PageResponse<Message>"));
        
        endpoints.add(new Endpoint("GET", "/messages/unread",
            "Get unread messages",
            true, null,
            Map.of("page", "page number", "size", "page size"),
            null,
            "PageResponse<Message>"));
        
        endpoints.add(new Endpoint("GET", "/messages/unread/count",
            "Get unread message count",
            true, null,
            null,
            null,
            "{ unreadCount }"));
        
        endpoints.add(new Endpoint("POST", "/messages",
            "Send a message",
            true, null,
            null,
            "Message object (receiver or community required)",
            "Message"));
        
        endpoints.add(new Endpoint("PUT", "/messages/{id}/read",
            "Mark message as read",
            true, null,
            null,
            null,
            "Success message"));
        
        endpoints.add(new Endpoint("GET", "/messages/contacts",
            "Get user's contacts (users they've messaged)",
            true, null,
            null,
            null,
            "{ contacts: List<User>, totalContacts: int }"));
        
        endpoints.add(new Endpoint("POST", "/messages/contact",
            "Send contact form message (public endpoint)",
            false, null,
            null,
            "{ name, email, topic, phone, message }",
            "Success message"));
        
        return endpoints;
    }
    
    /**
     * Get all community endpoints
     */
    public static List<Endpoint> getCommunityEndpoints() {
        List<Endpoint> endpoints = new ArrayList<>();
        
        endpoints.add(new Endpoint("GET", "/communities",
            "Get all communities (paginated)",
            false, null,
            Map.of("page", "page number", "size", "page size", "province", "province name", 
                   "district", "district name", "search", "search term", "popular", "true|false"),
            null,
            "PageResponse<Community>"));
        
        endpoints.add(new Endpoint("GET", "/communities/{id}",
            "Get community by ID",
            false, null,
            null,
            null,
            "Community"));
        
        endpoints.add(new Endpoint("POST", "/communities",
            "Create a community",
            true, null,
            null,
            "Community object",
            "Community"));
        
        endpoints.add(new Endpoint("PUT", "/communities/{id}",
            "Update community",
            true, null,
            null,
            "Community object with updated fields",
            "Community"));
        
        endpoints.add(new Endpoint("POST", "/communities/{id}/join",
            "Join a community",
            true, null,
            null,
            null,
            "Success message"));
        
        endpoints.add(new Endpoint("POST", "/communities/{id}/leave",
            "Leave a community",
            true, null,
            null,
            null,
            "Success message"));
        
        endpoints.add(new Endpoint("POST", "/communities/{id}/admins/{userId}",
            "Add admin to community",
            true, null,
            null,
            null,
            "Success message"));
        
        endpoints.add(new Endpoint("GET", "/communities/my-communities",
            "Get current user's communities",
            true, null,
            Map.of("page", "page number", "size", "page size"),
            null,
            "PageResponse<Community>"));
        
        endpoints.add(new Endpoint("GET", "/communities/stats",
            "Get community statistics",
            false, null,
            null,
            null,
            "{ totalCommunities, publicCommunities, communitiesByProvince }"));
        
        return endpoints;
    }
    
    /**
     * Get all endpoints grouped by controller
     */
    public static Map<String, List<Endpoint>> getAllEndpointsByController() {
        Map<String, List<Endpoint>> endpoints = new LinkedHashMap<>();
        endpoints.put("AuthController", getAuthEndpoints());
        endpoints.put("OTPAuthController", getOTPAuthEndpoints());
        endpoints.put("UserController", getUserEndpoints());
        endpoints.put("DonationController", getDonationEndpoints());
        endpoints.put("NGOController", getNGOEndpoints());
        endpoints.put("LocationController", getLocationEndpoints());
        endpoints.put("SearchController", getSearchEndpoints());
        endpoints.put("CategoryController", getCategoryEndpoints());
        endpoints.put("MessageController", getMessageEndpoints());
        endpoints.put("CommunityController", getCommunityEndpoints());
        return endpoints;
    }
    
    /**
     * Get all endpoints as a flat list
     */
    public static List<Endpoint> getAllEndpoints() {
        List<Endpoint> allEndpoints = new ArrayList<>();
        allEndpoints.addAll(getAuthEndpoints());
        allEndpoints.addAll(getOTPAuthEndpoints());
        allEndpoints.addAll(getUserEndpoints());
        allEndpoints.addAll(getDonationEndpoints());
        allEndpoints.addAll(getNGOEndpoints());
        allEndpoints.addAll(getLocationEndpoints());
        allEndpoints.addAll(getSearchEndpoints());
        allEndpoints.addAll(getCategoryEndpoints());
        allEndpoints.addAll(getMessageEndpoints());
        allEndpoints.addAll(getCommunityEndpoints());
        return allEndpoints;
    }
    
    /**
     * Find endpoints by path pattern
     */
    public static List<Endpoint> findEndpointsByPath(String pathPattern) {
        List<Endpoint> matches = new ArrayList<>();
        for (Endpoint endpoint : getAllEndpoints()) {
            if (endpoint.getPath().contains(pathPattern)) {
                matches.add(endpoint);
            }
        }
        return matches;
    }
    
    /**
     * Find endpoints by method
     */
    public static List<Endpoint> findEndpointsByMethod(String method) {
        List<Endpoint> matches = new ArrayList<>();
        for (Endpoint endpoint : getAllEndpoints()) {
            if (endpoint.getMethod().equalsIgnoreCase(method)) {
                matches.add(endpoint);
            }
        }
        return matches;
    }
    
    /**
     * Find endpoints that require a specific role
     */
    public static List<Endpoint> findEndpointsByRole(String role) {
        List<Endpoint> matches = new ArrayList<>();
        for (Endpoint endpoint : getAllEndpoints()) {
            if (endpoint.getRequiredRoles().contains(role)) {
                matches.add(endpoint);
            }
        }
        return matches;
    }
    
    /**
     * Get public endpoints (no authentication required)
     */
    public static List<Endpoint> getPublicEndpoints() {
        List<Endpoint> publicEndpoints = new ArrayList<>();
        for (Endpoint endpoint : getAllEndpoints()) {
            if (!endpoint.requiresAuth()) {
                publicEndpoints.add(endpoint);
            }
        }
        return publicEndpoints;
    }
    
    /**
     * Print all endpoints to console (for debugging/documentation)
     */
    public static void printAllEndpoints() {
        Map<String, List<Endpoint>> endpointsByController = getAllEndpointsByController();
        for (Map.Entry<String, List<Endpoint>> entry : endpointsByController.entrySet()) {
            System.out.println("\n=== " + entry.getKey() + " ===");
            for (Endpoint endpoint : entry.getValue()) {
                System.out.println(endpoint);
            }
        }
    }
}

