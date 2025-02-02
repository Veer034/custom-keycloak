//package com.convonest.keycloak;
//
//
//import org.keycloak.models.ClientModel;
//import org.keycloak.models.KeycloakSession;
//import org.keycloak.models.KeycloakSessionFactory;
//import org.keycloak.models.RealmModel;
//import org.keycloak.models.UserSessionModel;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.stream.Stream;
//
//import static com.convonest.keycloak.Constants.TARGET_REACT_CLIENT_ID;
//import static com.convonest.keycloak.SessionManagementUtil.handleExpiredSession;
//
//public class ExpiredSessionCheckerTask implements Runnable {
//    private static final Logger logger = LoggerFactory.getLogger(ExpiredSessionCheckerTask.class);
//
//    private final String loggedOutTopic;
//
//    private KeycloakSessionFactory sessionFactory;
//
//    public ExpiredSessionCheckerTask(KeycloakSessionFactory sessionFactory) {
//        this.sessionFactory = sessionFactory;
//        this.loggedOutTopic = KafkaManager.getLoggedOutTopic();
//        logger.info("ExpiredSessionCheckerTask initialized with logged out topic: {}", this.loggedOutTopic);
//    }
//
//    public void run() {
//        logger.info("Running scheduled task to check for expired user sessions...");
//        try (KeycloakSession session = sessionFactory.create()) {
//            session.getTransactionManager().begin();
//            for (RealmModel realm : session.realms().getRealmsStream().toList()) {
//                ClientModel targetClient = realm.getClientByClientId(TARGET_REACT_CLIENT_ID);
//                if (targetClient == null) {
//                    logger.warn("Client {} not found in realm {}", TARGET_REACT_CLIENT_ID, realm.getName());
//                    continue;
//                }
//
//                Stream<UserSessionModel> userSessions = session.sessions().getUserSessionsStream(realm, targetClient);
//
//                for (UserSessionModel userSession : userSessions.toList()) {
//                    if (isSessionExpired(userSession, realm) && isUserAssociatedWithClient(userSession, targetClient)) {
//                        handleExpiredSession(session, realm, userSession, "session_expired");
//                    }
//                }
//            }
//            session.getTransactionManager().commit();
//        } catch (Exception e) {
//            logger.error("Error in ExpiredSessionCheckerTask", e);
//        }
//    }
//
//
//    private boolean isSessionExpired(UserSessionModel userSession, RealmModel realm) {
//
//        long currentTime = System.currentTimeMillis() / 1000; // Convert to seconds
//        long refreshTokenTTL = realm.getSsoSessionIdleTimeout(); // Get refresh token timeout (idle session time)
//
//        long lastAccess = userSession.getLastSessionRefresh(); // Last action time
//        long idleTimeout = refreshTokenTTL; // Time until session expires due to inactivity
//        boolean isExpired = (currentTime - lastAccess) > idleTimeout;
//
//        logger.info("userSession: {} | Last Access: {} | Refresh Token TTL: {} |   Remaining Time: {} | " +
//                        "Expired: {}",
//                userSession.getUser().getUsername(), lastAccess, idleTimeout, (currentTime - lastAccess), isExpired);
//
//        return isExpired; // Remove only expired sessions
//    }
//
//    private boolean isUserAssociatedWithClient(UserSessionModel session, ClientModel client) {
//        return session.getAuthenticatedClientSessions().containsKey(client.getId());
//    }
//
//
////    @Override
////    public void run() {
////        logger.info("🔄 Running scheduled task to check for expired user sessions...");
////
////        try (KeycloakSession session = sessionFactory.create()) {
////            session.getTransactionManager().begin();
////
////            // Fetch all realms
////            List<RealmModel> realms = session.realms().getRealmsStream().collect(Collectors.toList());
////            for (RealmModel realm : realms) {
////                logger.info("🔍 Checking expired sessions for realm: {}", realm.getName());
////                checkExpiredSessions(session, realm);
////            }
////
////            session.getTransactionManager().commit();
////        } catch (Exception e) {
////            logger.error("❌ Error in ExpiredSessionCheckerTask", e);
////        }
////    }
////
////    private void checkExpiredSessions(KeycloakSession session, RealmModel realm) {
////        UserSessionProvider userSessionProvider = session.getProvider(UserSessionProvider.class);
////        long currentTime = System.currentTimeMillis() / 1000; // Convert to seconds
////        long refreshTokenTTL = realm.getSsoSessionIdleTimeout(); // Get refresh token timeout (idle session time)
////
////        ClientModel targetClient = realm.getClientByClientId(TARGET_REACT_CLIENT_ID);
////        if (targetClient == null) {
////            logger.warn("Client {} not found in realm {}", TARGET_REACT_CLIENT_ID, realm.getName());
////        }
////        // Stream through active user sessions
////        userSessionProvider.getUserSessionsStream(realm, targetClient, 0, Integer.MAX_VALUE)
////                .filter(userSession -> {
////
////                    long lastAccess = userSession.getLastSessionRefresh(); // Last action time
////                    long idleTimeout = refreshTokenTTL; // Time until session expires due to inactivity
////                    boolean isExpired = (currentTime - lastAccess) > idleTimeout;
////
////                    logger.info("userSession: {} | Last Access: {} | Refresh Token TTL: {} |   Remaining Time: {} | " +
////                                    "Expired: {}",
////                            userSession.getUser().getUsername(), lastAccess, idleTimeout, (currentTime - lastAccess), isExpired);
////
////                    return isExpired; // Remove only expired sessions
////                })
////                .forEach(userSession -> {
////                    logger.info("Removing expired session for user: {}", userSession.getUser().getUsername());
////                    session.sessions().removeUserSession(realm, userSession);
////                });
////    }
//
//}