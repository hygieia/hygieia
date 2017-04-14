package com.capitalone.dashboard.service;

import java.util.Collection;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.auth.DeleteLastAdminException;
import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.model.UserInfo;
import com.capitalone.dashboard.model.UserRole;
import com.capitalone.dashboard.repository.UserInfoRepository;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@Component
public class UserInfoServiceImpl implements UserInfoService {

	private UserInfoRepository userInfoRepository;
	
	@Autowired
	public UserInfoServiceImpl(UserInfoRepository userInfoRepository) {
		this.userInfoRepository = userInfoRepository;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities(String username, AuthType authType) {
		Collection<UserRole> roles = getUserInfo(username, authType).getAuthorities();
		return createAuthorities(roles);
	}
	
	@Override
	public UserInfo getUserInfo(String username, AuthType authType) {
		UserInfo userInfo = userInfoRepository.findByUsernameAndAuthType(username, authType);
		if(userInfo == null) {
			userInfo = createUserInfo(username, authType);
			userInfoRepository.save(userInfo);
		}
		
		// TODO: This will give the standard "admin" user admin privledges, might want
		// to bootstrap in an admin user, or something better than this.
		addAdminRoleToStandardAdminUser(userInfo);
		
		return userInfo;
	}
	
    @Override
    public Collection<UserInfo> getUsers() {
        return Sets.newHashSet(userInfoRepository.findAll());
    }
    
    @Override
    public UserInfo promoteToAdmin(String username, AuthType authType) {
        UserInfo user = userInfoRepository.findByUsernameAndAuthType(username, authType);
        if (user == null) {
            return null;
        }
        
        user.getAuthorities().add(UserRole.ROLE_ADMIN);
        UserInfo savedUser = userInfoRepository.save(user);
        return savedUser;
    }
    
    @Override
    public UserInfo demoteFromAdmin(String username, AuthType authType) {
        int numberOfAdmins = userInfoRepository.findByAuthoritiesIn(UserRole.ROLE_ADMIN).size();
        if(numberOfAdmins <= 1) {
            throw new DeleteLastAdminException();
        }
        UserInfo user = userInfoRepository.findByUsernameAndAuthType(username, authType);
        if (user == null) {
            return null;
        }
        
        user.getAuthorities().remove(UserRole.ROLE_ADMIN);
        UserInfo savedUser = userInfoRepository.save(user);
        return savedUser;
    }

	private UserInfo createUserInfo(String username, AuthType authType) {
		UserInfo userInfo = new UserInfo();
		userInfo.setUsername(username);
		userInfo.setAuthType(authType);
		userInfo.setAuthorities(Lists.newArrayList(UserRole.ROLE_USER));
		
		return userInfo;
	}

	private void addAdminRoleToStandardAdminUser(UserInfo userInfo) {
		if ("admin".equals(userInfo.getUsername()) && AuthType.STANDARD == userInfo.getAuthType()) {
			userInfo.getAuthorities().add(UserRole.ROLE_ADMIN);
		}
	}
	
	private Collection<? extends GrantedAuthority> createAuthorities(Collection<UserRole> authorities) {
		Collection<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>();
		authorities.forEach(authority -> {
			grantedAuthorities.add(new SimpleGrantedAuthority(authority.name())); 
		});
		
		return grantedAuthorities;
	}

}
