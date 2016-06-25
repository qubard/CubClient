package es.blueberrypancak.event;

public interface EventCancellable {

	public boolean isCancelled();
	
	public void setCancelled(boolean cancelled);
}
