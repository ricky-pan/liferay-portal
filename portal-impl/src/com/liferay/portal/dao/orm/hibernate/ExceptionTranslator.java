/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.dao.orm.hibernate;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.ORMException;
import com.liferay.portal.kernel.dao.orm.ObjectNotFoundException;
import com.liferay.portal.kernel.json.JSONSerializable;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.model.impl.UserImpl;

import org.hibernate.Session;
import org.hibernate.StaleObjectStateException;
import org.jabsorb.JSONSerializer;
import org.jabsorb.serializer.MarshallException;

/**
 * @author Brian Wing Shun Chan
 */
public class ExceptionTranslator {

	public static ORMException translate(Exception e) {
		if (e instanceof org.hibernate.ObjectNotFoundException) {
			return new ObjectNotFoundException(e);
		}
		else {
			return new ORMException(e);
		}
	}

	public static ORMException translate(
		Exception e, Session session, Object object) {

		if (e instanceof StaleObjectStateException) {
			BaseModel<?> baseModel = (BaseModel<?>)object;

			Object currentObject = session.get(
				object.getClass(), baseModel.getPrimaryKeyObj());

			try {
				Object jsonObject = _jsonSerializer.toJSON(object);
				Object currJsonObject = _jsonSerializer.toJSON(currentObject);
				return new ORMException(
					jsonObject + " is stale in comparison to " + currJsonObject, e);
			}
			catch (MarshallException e1) {
				e1.printStackTrace();
			}
		}

		return new ORMException(e);
	}

	private static User _obfuscate(Object object) {

		User user = (User)object;

		user.setPassword(StringPool.EIGHT_STARS);
		user.setDigest(StringPool.EIGHT_STARS);
		user.setReminderQueryQuestion(StringPool.EIGHT_STARS);
		user.setReminderQueryAnswer(StringPool.EIGHT_STARS);
		user.setScreenName(StringPool.EIGHT_STARS);
		user.setEmailAddress(StringPool.EIGHT_STARS);
		user.setFacebookId((user.getFacebookId() != 0) ? 0 : -1);
		user.setGoogleUserId(StringPool.EIGHT_STARS);
		user.setGreeting(StringPool.EIGHT_STARS);
		user.setFirstName(StringPool.EIGHT_STARS);
		user.setMiddleName(StringPool.EIGHT_STARS);
		user.setLastName(StringPool.EIGHT_STARS);
		user.setJobTitle(StringPool.EIGHT_STARS);

		return user;
	}

	private static final Log _log = LogFactoryUtil.getLog(ExceptionTranslator.class);
	private static JSONSerializer _jsonSerializer;

}