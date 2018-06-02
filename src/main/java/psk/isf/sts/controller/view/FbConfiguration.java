// package psk.isf.sts.controller.view;
//
// import javax.sql.DataSource;
//
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.context.annotation.Scope;
// import org.springframework.context.annotation.ScopedProxyMode;
// import org.springframework.core.env.Environment;
// import org.springframework.security.crypto.encrypt.Encryptors;
// import org.springframework.social.UserIdSource;
// import
// org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
// import org.springframework.social.config.annotation.EnableSocial;
// import org.springframework.social.config.annotation.SocialConfigurer;
// import org.springframework.social.connect.Connection;
// import org.springframework.social.connect.ConnectionFactoryLocator;
// import org.springframework.social.connect.ConnectionRepository;
// import org.springframework.social.connect.UsersConnectionRepository;
// import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
// import org.springframework.social.connect.web.ProviderSignInController;
// import org.springframework.social.facebook.api.Facebook;
// import org.springframework.social.facebook.connect.FacebookConnectionFactory;
// import org.springframework.social.security.AuthenticationNameUserIdSource;
//
// import psk.isf.sts.service.authorization.FacebookConnectionSignup;
// import psk.isf.sts.service.authorization.FacebookSignInAdapter;
//
// @Configuration
// @EnableSocial
// public class FbConfiguration implements SocialConfigurer {
//
// @Autowired
// private DataSource dataSource;
//
// @Autowired
// private FacebookConnectionSignup facebookConnectionSignup;
//
// @Autowired
// private FacebookSignInAdapter facebookSignInAdapter;
//
// @Override
// public void addConnectionFactories(ConnectionFactoryConfigurer cfConfig,
// Environment env) {
// cfConfig.addConnectionFactory(new
// FacebookConnectionFactory(env.getProperty("facebook.appKey"),
// env.getProperty("facebook.appSecret")));
// }
//
// @Override
// public UsersConnectionRepository
// getUsersConnectionRepository(ConnectionFactoryLocator
// connectionFactoryLocator) {
// JdbcUsersConnectionRepository repository = new
// JdbcUsersConnectionRepository(dataSource,
// connectionFactoryLocator, Encryptors.noOpText());
// repository.setConnectionSignUp(facebookConnectionSignup);
// return repository;
// }
//
// @Bean
// @Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
// public Facebook facebook(ConnectionRepository repository) {
//
// Connection<Facebook> connection =
// repository.findPrimaryConnection(Facebook.class);
// return connection != null ? connection.getApi() : null;
// }
//
// @Bean
// public ProviderSignInController
// providerSignInController(ConnectionFactoryLocator connectionFactoryLocator,
// UsersConnectionRepository usersConnectionRepository) {
// return new ProviderSignInController(connectionFactoryLocator,
// usersConnectionRepository, facebookSignInAdapter);
// }
//
// @Override
// public UserIdSource getUserIdSource() {
// return new AuthenticationNameUserIdSource();
// };
// }
