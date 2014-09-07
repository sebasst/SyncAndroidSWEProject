package com.york.cs.server.exception;
public class IncorrectInformation extends RuntimeException {

		private static final long serialVersionUID = 1L;

		public IncorrectInformation() {
			super();

		}

		public IncorrectInformation(String arg0, Throwable arg1, boolean arg2,
				boolean arg3) {
			super(arg0, arg1, arg2, arg3);

		}

		public IncorrectInformation(String arg0, Throwable arg1) {
			super(arg0, arg1);

		}

		public IncorrectInformation(String arg0) {
			super(arg0);
		}

		public IncorrectInformation(Throwable arg0) {
			super(arg0);

		}

	}