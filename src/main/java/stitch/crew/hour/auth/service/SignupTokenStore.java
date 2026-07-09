package stitch.crew.hour.auth.service;

import java.util.Optional;

import stitch.crew.hour.auth.dto.OAuthSignupPayload;

public interface SignupTokenStore {

	String save(OAuthSignupPayload payload);

	Optional<OAuthSignupPayload> find(String signupToken);

	void delete(String signupToken);
}
