package org.pojava.validation;

/*
 Copyright 2008 John Pile

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

import java.util.ArrayList;
import java.util.List;

public class ValidationException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private List alerts = new ArrayList();

	public ValidationException() {
	}

	public ValidationException(Alert alert) {
		alerts.add(alert);
	}

	public void add(Alert alert) {
		alerts.add(alert);
	}
}
