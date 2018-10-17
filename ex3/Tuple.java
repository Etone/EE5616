package ex3;

/**
	 * Tuple Class for result from time measured method
	 */
	public class Tuple<R,D> {
		private R result;
		private D duration;
		
		public Tuple(R result, D duration) {

			this.result = result;
			this.duration = duration;
		}
		
		public D getDuration() {
			return duration;
		}
		
		public R getResult() {
			return result;
		}
		
		public void setDuration(D duration) {
			this.duration = duration;
		}
		
		public void setResult(R result) {

			this.result = result;
		}
	}
